package com.app.pcbook.service;

import com.app.pcbook.*;
import com.app.pcbook.exception.AlreadyExistsException;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;
import java.util.logging.Logger;

public class LaptopService extends LaptopServiceGrpc.LaptopServiceImplBase {
    private static final Logger logger = Logger.getLogger(LaptopService.class.getName());

    private LaptopStore laptopStore;

    public LaptopService(LaptopStore laptopStore) {
        this.laptopStore = laptopStore;
    }

    @Override
    public void createLaptop(CreateLaptopRequest request, StreamObserver<CreateLaptopResponse> responseObserver) {
        Laptop laptop = request.getLaptop();
        String id = laptop.getId();

        logger.info("got a create-laptop request with ID: " + id);

        UUID uuid;
        if(id.isEmpty()) {
            uuid = UUID.randomUUID();
        } else {
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException exception) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription(exception.getMessage())
                                .asRuntimeException()
                );
                return;
            }
        }

        if (Context.current().isCancelled()) {
            logger.info("request is cancelled");
            responseObserver.onError(
                    Status.CANCELLED
                            .withDescription("request is cancelled")
                            .asRuntimeException()
            );
            return;
        }


        Laptop other = laptop.toBuilder().setId(uuid.toString()).build();
        try {
            laptopStore.save(other);
        } catch (AlreadyExistsException alreadyExistsException) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription(alreadyExistsException.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception exception) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(exception.getMessage())
                            .asRuntimeException()
            );
        }

        CreateLaptopResponse response = CreateLaptopResponse.newBuilder()
                .setId(other.getId()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("saved laptop with ID: " + other.getId());
    }

    public void searchLaptop(SearchLaptopRequest request, StreamObserver<SearchLaptopResponse> responseObserver) {
        Filter filter = request.getFilter();

        logger.info("got a search-request with filter " + filter);

        laptopStore.search(Context.current(), filter, new LaptopStream() {
            @Override
            public void send(Laptop laptop) {
                logger.info("Laptop with ID : " + laptop.getId() + " is found");
                SearchLaptopResponse response = SearchLaptopResponse
                        .newBuilder()
                        .setLaptop(laptop)
                        .build();

                responseObserver.onNext(response);
            }
        });

        responseObserver.onCompleted();
        logger.info("search laptop completed");
    }
}
