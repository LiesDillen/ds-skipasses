package com.example.springsoap;

import io.skipass.gt.webservice.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.example.springsoap.exception.*;
import org.springframework.ws.soap.SoapFaultException;

import java.io.IOException;
import java.util.List;


@Endpoint
public class SkipassEndpoint
{
    private static final String NAMESPACE_URI = "http://skipass.io/gt/webservice";

    private final SkipassRepository skipassrepo;

    @Autowired
    public SkipassEndpoint(SkipassRepository skipassrepo) {
        this.skipassrepo = skipassrepo;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSkipassByIdRequest")
    @ResponsePayload
    public GetSkipassByIdResponse getSkipassByIdResponse(@RequestPayload GetSkipassByIdRequest request)
    {
        GetSkipassByIdResponse response = new GetSkipassByIdResponse();
        response.setSkipass(skipassrepo.findSkipassById(request.getId()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStockRequest")
    @ResponsePayload
    public GetStockResponse getStockResponse(@RequestPayload GetStockRequest request)
    {
        GetStockResponse response = new GetStockResponse();
        List<Skipass> stockList = skipassrepo.getFullStock();
        response.getStock().addAll(stockList);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "prepareRequest")
    @ResponsePayload
    public PrepareResponse prepare(@RequestPayload PrepareRequest request)
    {
        PrepareResponse response = new PrepareResponse();
        try {
            Vote vote = skipassrepo.sendVote(request.getTransactionId(), request.getOrders());
            response.setVote(vote);
        } catch (IOException e) {
            throw new SoapFaultException("Internal server error: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "bookSkipassRequest")
    @ResponsePayload
    public BookSkipassResponse bookSkipass(@RequestPayload BookSkipassRequest request)
    {
        BookSkipassResponse response = new BookSkipassResponse();
        try {
            ProtocolMessage ack = skipassrepo.bookItem(request.getTransactionId(), request.getDecision(), request.getOrders());
            response.setAck(ack);
        } catch (IOException e) {
            throw new SoapFaultException("Internal server error: " + e.getMessage());
        } catch (InsufficientStockException e) {
            throw new SoapFaultException("Insufficient stock: " + e.getMessage());
        } catch (InvalidStockIdException e) {
            throw new SoapFaultException("Invalid stock ID: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "rollBackRequest")
    @ResponsePayload
    public void rollBack(@RequestPayload RollBackRequest request)
    {
        skipassrepo.rollBack(request.getTransactionId());
    }
}