package com.app.pcbook.service;

import com.app.pcbook.Filter;
import com.app.pcbook.Laptop;
import com.app.pcbook.Memory;
import com.app.pcbook.exception.AlreadyExistsException;
import io.grpc.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class InMemoryLaptopStore implements LaptopStore{
    private ConcurrentMap<String, Laptop> data;

    private static final Logger logger = Logger.getLogger(InMemoryLaptopStore.class.getName());

    public InMemoryLaptopStore() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Laptop laptop) throws RuntimeException {
        if(data.containsKey(laptop.getId())) {
            throw new AlreadyExistsException("Laptop with ID " + laptop.getId() + " already exists");
        }

        Laptop other = laptop.toBuilder().build();
        data.put(other.getId(), other);
    }

    @Override
    public Laptop findById(String id) {
        if(!data.containsKey(id)) {
            return null;
        }
        Laptop laptop = data.get(id);
        return laptop.toBuilder().build();
    }

    @Override
    public void search(Context ctx, Filter filter, LaptopStream stream) {
        for(Map.Entry<String, Laptop> entry: data.entrySet()) {
            if(ctx.isCancelled()) {
                logger.info("Context is cancelled");
                return;
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            Laptop laptop = entry.getValue();
            if(isQualified(filter, laptop)) {
                stream.send(laptop);
            }
        }
    }

    private boolean isQualified(Filter filter, Laptop laptop) {
        if(laptop.getPriceUsd() > filter.getMaxPriceUsd()) {
            return false;
        }
        if(laptop.getCpu().getNumberCores() < filter.getMinCpuCores()) {
            return false;
        }

        if(laptop.getCpu().getMinGhz() < filter.getMinCpuGhz()) {
            return false;
        }

        if(toBit(laptop.getRam()) < toBit(filter.getMinRam())) {
            return false;
        }
        return true;
    }

    private long toBit(Memory memory) {
        long value = memory.getValue();

        switch (memory.getUnit()) {
            case BIT:
                return value;
            case BYTE:
                return value << 3;
            case KILOBYTE:
                return value << 13;
            case MEGABYTE:
                return value << 23;
            case GIGABYTE:
                return value << 33;
            case TERABYTE:
                return value << 43;
            default:
                return 0;
        }
    }
}
