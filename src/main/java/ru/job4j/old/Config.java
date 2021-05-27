package ru.job4j.old;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Config {
    private final String path;
    private final Map<String, String> values = new HashMap<>();

    public Config(final String path) {
        this.path = path;
    }

    public void load() {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            in.lines()
                    .filter(line -> !line.isEmpty() && !line.startsWith("#")
                            && line.contains("="))
                    .forEach(line -> {
                        String[] array = line.split("=", 2);
                        if (array[1].isEmpty()) {
                            throw new IllegalArgumentException();
                        } else {
                            values.put(array[0], array[1]);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String value(String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        StringJoiner out = new StringJoiner(System.lineSeparator());
        try (BufferedReader read = new BufferedReader(new FileReader(this.path))) {
            read.lines().forEach(out::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static void main(String[] args) {
        Config cf = new Config("app.properties");
        cf.load();
        for (Map.Entry<String, String> entry : cf.values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " = value: " + value);
        }
    }
}
