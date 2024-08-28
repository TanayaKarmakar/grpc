package com.app.pcbook.service;

import com.app.pcbook.Laptop;

public interface LaptopStore {
    void save(Laptop laptop) throws RuntimeException;
    Laptop findById(String id);
}
