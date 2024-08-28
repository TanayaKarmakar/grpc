package com.app.pcbook.serializer;

import com.app.pcbook.Laptop;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class Serializer {
    public void writeBinaryFile(Laptop laptop, String fileName) throws IOException {
        FileOutputStream outStream = new FileOutputStream(fileName);
        laptop.writeTo(outStream);
        outStream.close();
    }

    public Laptop readBinaryFile(String fileName) throws IOException {
        FileInputStream inStream = new FileInputStream(fileName);
        Laptop laptop = Laptop.parseFrom(inStream);
        inStream.close();
        return laptop;
    }

    public void writeJSONFile(Laptop laptop, String fileName) throws IOException {
        JsonFormat.Printer printer = JsonFormat.printer()
                .includingDefaultValueFields(new HashSet<>())
                .preservingProtoFieldNames();

        String jsonString = printer.print(laptop);

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();
    }

    public static void main(String[] args) throws IOException {
        Serializer serializer = new Serializer();
        Laptop laptop = serializer.readBinaryFile("laptop.bin");
        serializer.writeJSONFile(laptop, "laptop.json");
    }
}
