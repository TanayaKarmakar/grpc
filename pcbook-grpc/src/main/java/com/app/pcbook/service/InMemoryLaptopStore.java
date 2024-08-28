package com.app.pcbook.service;

import com.app.pcbook.Laptop;
import com.app.pcbook.exception.AlreadyExistsException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryLaptopStore implements LaptopStore{
    private ConcurrentMap<String, Laptop> data;

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
}
