# Use an appropriate Maven and JDK base image
FROM maven:3-openjdk-17-slim

# Set the working directory inside the container
WORKDIR /app

Copy the entire project directory into the container
COPY . .
COPY stock.csv /app/

RUN chmod +r /app/stock.csv

# Build the application with Maven
RUN mvn clean package

Expose the port your application runs on
EXPOSE 8081

Run the Spring Boot application
CMD ["mvn", "spring-boot"]
