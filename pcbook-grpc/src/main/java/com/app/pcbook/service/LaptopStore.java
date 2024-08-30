package com.app.pcbook.service;

import com.app.pcbook.Filter;
import com.app.pcbook.Laptop;
import io.grpc.Context;

public interface LaptopStore {
    void save(Laptop laptop) throws RuntimeException;
    Laptop findById(String id);

    void search(Context ctx, Filter filter, LaptopStream stream);
}

