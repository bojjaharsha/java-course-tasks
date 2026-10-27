package com.epam.files.task1;

import java.io.*;

public class SerializationUtil {

    // Serialize object to file using try-with-resources
    public static void serialize(Object object, String filePath) throws IOException {
        try (var fos = new FileOutputStream(filePath);
             var oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        }
    }

    // Deserialize object from file using try-with-resources
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String filePath) throws IOException, ClassNotFoundException {
        try (var fis = new FileInputStream(filePath);
             var ois = new ObjectInputStream(fis)) {
            return (T) ois.readObject();
        }
    }
}
