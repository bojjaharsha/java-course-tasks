package com.epam.files.task3;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class FastFileMover {

    // 1. FileStreams (byte-by-byte)
    public static void moveWithStreams(Path source, Path target) throws IOException {
        try (var in = new FileInputStream(source.toFile());
             var out = new FileOutputStream(target.toFile())) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }
        Files.delete(source);
    }

    // 2. FileStreams with 100 KB buffer
    public static void moveWithBufferedStreams(Path source, Path target) throws IOException {
        byte[] buffer = new byte[100 * 1024]; // 100 KB
        try (var in = new FileInputStream(source.toFile());
             var out = new FileOutputStream(target.toFile())) {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        Files.delete(source);
    }

    // 3. FileChannel (zero-copy transfer)
    public static void moveWithChannel(Path source, Path target) throws IOException {
        try (var inChannel = FileChannel.open(source, StandardOpenOption.READ);
             var outChannel = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            long size = inChannel.size();
            long transferred = 0;
            while (transferred < size) {
                transferred += inChannel.transferTo(transferred, size - transferred, outChannel);
            }
        }
        Files.delete(source);
    }

    // 4. NIO 2 Files API
    public static void moveWithNio2(Path source, Path target) throws IOException {
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
