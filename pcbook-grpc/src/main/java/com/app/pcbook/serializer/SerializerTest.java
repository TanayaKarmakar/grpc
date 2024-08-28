package com.app.pcbook.serializer;

import com.app.pcbook.Generator;
import com.app.pcbook.Laptop;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SerializerTest {
    @Test
    public void testWriteAndReadBinaryFile() throws IOException {
        String binaryFile = "laptop.bin";

        Laptop laptop1 = new Generator().newLapTop();
        Serializer serializer = new Serializer();
        serializer.writeBinaryFile(laptop1, binaryFile);

        Laptop laptop2 = serializer.readBinaryFile(binaryFile);
        Assert.assertEquals(laptop1, laptop2);
    }
}
