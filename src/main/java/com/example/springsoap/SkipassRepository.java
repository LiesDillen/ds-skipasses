package com.example.springsoap;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.example.springsoap.exception.*;
import io.skipass.gt.webservice.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class SkipassRepository
{
    private static final String STOCK_FILE_PATH = "stock.csv";
    private static final Map<Integer, Skipass> stock = new HashMap<>();
    private static final Map<String, List<Order>> transactionChanges = new HashMap<>();
    private static final Map<Integer, Lock> stockLocks = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SkipassRepository.class);

    @PostConstruct
    public void initData()
    {
        logger.info("DEBUG: start initialising data");
        
        try (BufferedReader br = new BufferedReader(new FileReader(STOCK_FILE_PATH))) {
            br.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                Passtype passtype = Passtype.valueOf(parts[1].toUpperCase());
                float price = Float.parseFloat(parts[2]);
                int availableAmount = Integer.parseInt(parts[3]);
                Skipass s = new Skipass();
                s.setId(id);
                s.setSkipassType(passtype);
                s.setPrice(price);
                s.setAvailableAmount(availableAmount);
                stock.put(id, s);
                stockLocks.put(id, new ReentrantLock());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Skipass findSkipassById(Integer id)
    {
        Assert.notNull(id, "The skipass' id must not be null");
        logger.info("DEBUG: reading from stock.csv");
        return stock.get(id);
    }

    public List<Skipass> getFullStock()
    {
        return new ArrayList<>(stock.values());
    }

    public Vote sendVote(String transactionId, List<Order> orders) throws IOException
    {
        logger.info("2PC - {} - Vote request received.", transactionId);

        Vote vote = new Vote();
        vote.setVote(ProtocolMessage.VOTE_COMMIT);
        vote.setError("No error, transaction prepared.");

        File stockFile = new File(STOCK_FILE_PATH);
        Map<Passtype, Integer> stockMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(stockFile))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Passtype passtype = Passtype.valueOf(parts[1].toUpperCase());
                int stockCount = Integer.parseInt(parts[3]);
                stockMap.put(passtype, stockCount);
            }
        } catch (FileNotFoundException e) {
            logger.error("Failed to load stock.csv in sendVote - file not found.", e);
            vote.setVote(ProtocolMessage.VOTE_ABORT);
            vote.setError("Could not read from stock");
            return vote;
        }

        for (Order order : orders) {
            Passtype passtype = stock.get(order.getStockId()).getSkipassType();
            if (passtype == null) {
                vote.setVote(ProtocolMessage.VOTE_ABORT);
                vote.setError("Invalid stock ID: " + order.getStockId());
                logger.error("2PC - {} - Requested stock ID {} invalid for vote phase.", transactionId, order.getStockId());
            }

            int stockCount = stockMap.getOrDefault(passtype, 0);
            if (stockCount < order.getAmount()) {
                vote.setVote(ProtocolMessage.VOTE_ABORT);
                vote.setError("Insufficient stock for pass type: " + passtype + " with stock ID: " + order.getStockId());
                logger.error("2PC - {} - Insufficient stock for requested type {} for vote phase.", transactionId, passtype);
            }
        }

        for (Order order : orders) {
            Lock lock = stockLocks.get(order.getStockId());
            if (lock != null) {
                lock.lock();
                logger.info("2PC - Stock locked");
            }
        }

        logger.info("2PC - {} - Vote given: {}.", transactionId, vote.getVote());

        return vote;
    }


    public ProtocolMessage bookItem(String transactionId, ProtocolMessage decision, List<Order> orders) throws IOException, InsufficientStockException, InvalidStockIdException {
        logger.info("2PC - {} - Received booking request with decision: {}.", transactionId, decision);

        if (decision != ProtocolMessage.GLOBAL_COMMIT)
        {
            for (Order order : orders) {
                ReentrantLock lock = (ReentrantLock) stockLocks.get(order.getStockId());
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    logger.info("2PC - Stock unlocked 1");
                }
            }
            return ProtocolMessage.ACKNOWLEDGE;
        }

        File tempFile = new File(STOCK_FILE_PATH + ".tmp");
        File stockFile = new File(STOCK_FILE_PATH);

        Map<Passtype, Integer> requiredStockUpdates = new HashMap<>();
        for (Order order : orders) {
            Passtype passtype = stock.get(order.getStockId()).getSkipassType();
            if (passtype != null) {
                requiredStockUpdates.put(passtype, requiredStockUpdates.getOrDefault(passtype, 0) + order.getAmount());
            } else {
                logger.error("2PC - {} - Requested stock ID {} invalid for commit phase.", transactionId, order.getStockId());
                throw new InvalidStockIdException("Invalid stock ID: " + order.getStockId());
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(stockFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            bw.write(br.readLine() + "\n"); // Write header

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                Passtype currentPasstype = Passtype.valueOf(parts[1].toUpperCase());
                float price = Float.parseFloat(parts[2]);
                int stockCount = Integer.parseInt(parts[3]);

                if (requiredStockUpdates.containsKey(currentPasstype)) {
                    int requiredAmount = requiredStockUpdates.get(currentPasstype);
                    if (stockCount >= requiredAmount) {
                        stockCount -= requiredAmount;
                        requiredStockUpdates.remove(currentPasstype);
                    } else {
                        logger.error("2PC - {} - Insufficient stock for requested type {} for commit phase.", transactionId, currentPasstype);
                        throw new InsufficientStockException("Insufficient stock for pass type: " + currentPasstype);
                    }
                }
                bw.write(id + "," + currentPasstype.toString().toLowerCase() + "," + price + "," + stockCount + "\n");
            }
        }
        Files.move(tempFile.toPath(), stockFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        transactionChanges.put(transactionId, orders);
        logger.info("2PC - Changes made for transactionId: {}", transactionId);

        for (Order order : orders) {
            ReentrantLock lock = (ReentrantLock) stockLocks.get(order.getStockId());
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                logger.info("2PC - Stock unlocked 2");
            }
        }

        return ProtocolMessage.ACKNOWLEDGE;
    }


    public void rollBack(String transactionId)
    {
        logger.info("2PC - {} - Rollback request received.", transactionId);

        List<Order> orders = transactionChanges.get(transactionId);

        if (!transactionChanges.containsKey(transactionId))
        {
            logger.warn("2PC - {} - No changes recorded for rollback.", transactionId);
        } else {

            File tempFile = new File(STOCK_FILE_PATH + ".tmp");
            File stockFile = new File(STOCK_FILE_PATH);

            Map<Passtype, Integer> rollbackStockUpdates = new HashMap<>();
            for (Order order : orders) {
                Passtype passtype = stock.get(order.getStockId()).getSkipassType();
                if (passtype != null) {
                    rollbackStockUpdates.put(passtype, rollbackStockUpdates.getOrDefault(passtype, 0) + order.getAmount());
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(stockFile));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                bw.write(br.readLine() + "\n"); // Write header

                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    Passtype currentPasstype = Passtype.valueOf(parts[1].toUpperCase());
                    float price = Float.parseFloat(parts[2]);
                    int stockCount = Integer.parseInt(parts[3]);

                    if (rollbackStockUpdates.containsKey(currentPasstype)) {
                        stockCount += rollbackStockUpdates.get(currentPasstype);
                        rollbackStockUpdates.remove(currentPasstype);
                    }
                    bw.write(id + "," + currentPasstype.toString().toLowerCase() + "," + price + "," + stockCount + "\n");
                }
            } catch (IOException e) {
                logger.error("2PC - {} - Rollback failed.", transactionId, e);
                return;
            }
            try {
                Files.move(tempFile.toPath(), stockFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("2PC - {} - Rollback successful.", transactionId);
            } catch (IOException e) {
                logger.error("2PC - {} - Rollback move failed.", transactionId, e);
            }

            // Remove the changes from the log
            transactionChanges.remove(transactionId);
        }

        for (Order order : orders) {
            ReentrantLock lock = (ReentrantLock) stockLocks.get(order.getStockId());
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                logger.info("2PC - Stock unlocked");
            }
        }
    }
}