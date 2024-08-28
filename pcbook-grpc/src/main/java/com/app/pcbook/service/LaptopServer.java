package com.app.pcbook.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class LaptopServer {
    private static final Logger logger = Logger.getLogger(LaptopServer.class.getName());

    private final int port;
    private final Server server;

    public LaptopServer(int port, LaptopStore laptopStore) {
        this(ServerBuilder.forPort(port), port, laptopStore);
    }

    public LaptopServer(ServerBuilder serverBuilder, int port, LaptopStore laptopStore) {
        this.port = port;
        LaptopService laptopService = new LaptopService(laptopStore);
        server = serverBuilder.addService(laptopService).build();
    }

    public void start() throws IOException {
        server.start();
        logger.info("server started on port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                System.err.println("Shut down GRPC server because JVM shuts down");
                try {
                    LaptopServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("server shuts down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if(server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        LaptopStore store = new InMemoryLaptopStore();
        LaptopServer laptopServer = new LaptopServer(8080, store);

        laptopServer.start();
        laptopServer.blockUntilShutdown();
    }
}
