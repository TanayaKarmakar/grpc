package com.app.pcbook.service;

import com.app.pcbook.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaptopClient {
    private static final Logger logger = Logger.getLogger(LaptopClient.class.getName());

    private final ManagedChannel managedChannel;
    private final LaptopServiceGrpc.LaptopServiceBlockingStub stub;


    public LaptopClient(String host, int port) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        stub = LaptopServiceGrpc.newBlockingStub(managedChannel);
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createLaptop(Laptop laptop) {
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder()
                .setLaptop(laptop)
                .build();

        CreateLaptopResponse response = CreateLaptopResponse.getDefaultInstance();

        try {
            response = stub.createLaptop(request);
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "request failed" + exception.getMessage());
            return;
        }
        logger.info("Laptop created with ID: " + response.getId());

    }

    public static void main(String[] args) throws InterruptedException {
        LaptopClient laptopClient = new LaptopClient("0.0.0.0", 8080);
        Generator generator = new Generator();
        Laptop laptop = generator.newLapTop();

        try {
           laptopClient.createLaptop(laptop);
        } finally {
            laptopClient.shutdown();
        }

    }
}
