package com.app.pcbook;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class Generator {
    private Random random;

    public Generator() {
        random = new Random();
    }

    public Keyboard newKeyBoard() {
        return Keyboard.newBuilder()
                .setLayout(randomKeyboardLayout())
                .setBacklit(random.nextBoolean())
                .build();
    }

    public CPU newCPU() {
        String brand = randomCPUBrand();
        String name = randomCPUName(brand);
        int numberCores = randomInt(2, 8);
        int numberThreads = randomInt(numberCores, 12);

        double minGhz = randomDouble(2.0, 3.5);
        double maxGhz = randomDouble(minGhz, 5.0);

        return CPU.newBuilder()
                .setBrand(brand)
                .setName(name)
                .setNumberCores(numberCores)
                .setNumberThreads(numberThreads)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .build();
    }

    public GPU newGPU() {
        String brand = randomGPUBrand();
        String name = randomGPUName(brand);

        double minGhz = randomDouble(1.0, 1.5);
        double maxGhz = randomDouble(minGhz, 2.0);

        Memory memory = Memory.newBuilder()
                .setValue(randomInt(2, 6))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();

        return GPU.newBuilder()
                .setBrand(brand)
                .setName(name)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .setMemory(memory)
                .build();
    }

    public Memory newRAM() {
        return Memory.newBuilder()
                .setValue(randomInt(4, 64))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
    }

    public Storage newSSD() {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(128, 1024))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();

        return Storage.newBuilder()
                .setDriver(Storage.Driver.SSD)
                .setMemory(memory)
                .build();
    }

    public Storage newHDD() {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(1, 6))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();

        return Storage.newBuilder()
                .setDriver(Storage.Driver.HDD)
                .setMemory(memory)
                .build();
    }

    public Screen newScreen() {
        int height = randomInt(1080, 4320);
        int width = height * 16 / 9;

        Screen.Resolution resolution = Screen.Resolution.newBuilder()
                .setHeight(height)
                .setWidth(width)
                .build();

        return Screen.newBuilder()
                .setSizeInch(randomFloat(13, 17))
                .setResolution(resolution)
                .setPanel(randomScreenPanel())
                .setMultitouch(random.nextBoolean())
                .build();
    }

    public Laptop newLapTop() {
        String brand = randomLaptopBrand();
        String name = randomLaptopName(brand);

        double weightKg = randomDouble(1.0, 3.0);
        double priceUsd = randomDouble(1500, 3500);
        int releaseYear = randomInt(2015, 2024);
        return Laptop.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setBrand(brand)
                .setName(name)
                .setWeightKg(weightKg)
                .setPriceUsd(priceUsd)
                .setReleaseYear(releaseYear)
                .setKeyboard(newKeyBoard())
                .setCpu(newCPU())
                .setRam(newRAM())
                .addStorages(newSSD())
                .addStorages(newHDD())
                .addGpus(newGPU())
                .setUpdatedAt(timeStampNow())
                .build();
    }

    private Timestamp timeStampNow() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }

    private String randomLaptopName(String brand) {
        switch (brand) {
            case "Apple":
                return randomStringFromSet("Macbook Air", "Macbook Pro");
            case "Dell":
                return randomStringFromSet("Latitude", "Vostro", "XPS", "Alienware");
            default:
                return randomStringFromSet("Thinkpad X1", "Thinkpad P1", "Thinkpad P53");
        }
    }


    private String randomLaptopBrand() {
        return randomStringFromSet("Apple","Dell","Lenovo");
    }

    private Screen.Panel randomScreenPanel() {
        if(random.nextBoolean()) {
            return Screen.Panel.IPS;
        }
        return Screen.Panel.OLED;
    }

    private float randomFloat(int min, int max) {
        return min + random.nextFloat() * (max - min);
    }


    private String randomGPUName(String brand) {
        if(brand.equals("NVIDIA")) {
            return randomStringFromSet("RTX 2060",
                    "RTX 2070",
                    "GTX 1660-Ti",
                    "GTX 1070");
        }
        return randomStringFromSet("RX 590",
                "RX 580",
                "RX 5700-XT",
                "RX Vega-56");
    }


    private String randomGPUBrand() {
        return randomStringFromSet("NVIDIA", "AMD");
    }


    private double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private String randomCPUName(String brand) {
        if(brand.equals("Intel")) {
            return randomStringFromSet("Xeon-E-2286M",
                    "Core i9-9980HK",
                    "Core i7-9750H",
                    "Core i5-9400F",
                    "Core i3-1005G1");
        }
        return randomStringFromSet(
                "Ryzen 7 PRO 2700U",
                "Ryzen 5 PRO 3500U",
                "Ryzen 3 PRO 3200GE"
        );
    }

    private String randomCPUBrand() {
        return randomStringFromSet("Intel", "AMD");
    }

    private String randomStringFromSet(String ...brands) {
        int len = brands.length;
        if(len == 0)
            return "";
        return brands[random.nextInt(len)];
    }

    private Keyboard.Layout randomKeyboardLayout() {
        switch(random.nextInt(3)) {
            case 1:
                return Keyboard.Layout.QWERTY;
            case 2:
                return Keyboard.Layout.QWERTZ;
            default:
                return Keyboard.Layout.AZERTY;
        }
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        Laptop laptop = generator.newLapTop();

        System.out.println(laptop);
    }
}
