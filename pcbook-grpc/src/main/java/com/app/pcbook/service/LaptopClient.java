package com.app.pcbook.service;

import com.app.pcbook.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;
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

    private void searchLaptop(Filter filter) {
        logger.info("search started");

        SearchLaptopRequest searchLaptopRequest = SearchLaptopRequest
                .newBuilder()
                .setFilter(filter)
                .build();

        try {
            Iterator<SearchLaptopResponse> responseIterator = stub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .searchLaptop(searchLaptopRequest);

            while (responseIterator.hasNext()) {
                SearchLaptopResponse response = responseIterator.next();

                Laptop laptop = response.getLaptop();

                logger.info("Laptop found - " + laptop.getId());
            }
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Request failed " + exception.getMessage());
            return;
        }

        logger.info("search completed");
    }

    public static void main(String[] args) throws InterruptedException {
        LaptopClient laptopClient = new LaptopClient("0.0.0.0", 8080);
        Generator generator = new Generator();
        Laptop laptop = generator.newLapTop();

        try {
            for(int i = 0; i < 10; i++) {
                laptopClient.createLaptop(laptop);
            }

            Memory minRam = Memory
                    .newBuilder()
                    .setValue(8)
                    .setUnit(Memory.Unit.GIGABYTE)
                    .build();


            Filter filter = Filter.newBuilder()
                    .setMaxPriceUsd(3000)
                    .setMinCpuCores(4)
                    .setMinCpuGhz(2.5)
                    .setMinRam(minRam)
                    .build();


            laptopClient.searchLaptop(filter);

        } finally {
            laptopClient.shutdown();
        }

    }
}
