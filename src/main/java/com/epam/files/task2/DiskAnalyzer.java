package com.epam.files.task2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class DiskAnalyzer {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java DiskAnalyzer <path> <functionNumber: 1-4>");
            return;
        }

        String pathStr = args[0];
        int functionNum;
        try {
            functionNum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: Function number must be an integer between 1 and 4.");
            return;
        }

        Path startPath = Paths.get(pathStr);
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.out.println("Error: Directory does not exist: " + pathStr);
            return;
        }

        String outputFile = "disk_analysis_result.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            switch (functionNum) {
                case 1 -> findMaxSFile(startPath, writer);
                case 2 -> printTop5LargestFiles(startPath, writer);
                case 3 -> printAverageFileSize(startPath, writer);
                case 4 -> countByAlphabet(startPath, writer);
                default -> writer.write("Invalid function number! Choose 1, 2, 3, or 4.\n");
            }
            System.out.println("Analysis completed. Results written to: " + outputFile);
        } catch (IOException e) {
            System.err.println("File I/O error: " + e.getMessage());
        }
    }

    // 1. Search for file with maximum number of 's' in its name
    private static void findMaxSFile(Path path, BufferedWriter writer) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            Path bestFile = stream.filter(Files::isRegularFile)
                    .max(Comparator.comparingInt(p -> countLetterS(p.getFileName().toString())))
                    .orElse(null);

            if (bestFile != null) {
                writer.write("File with most 's': " + bestFile.toAbsolutePath() + "\n");
            } else {
                writer.write("No files found.\n");
            }
        }
    }

    private static int countLetterS(String name) {
        int count = 0;
        for (char c : name.toLowerCase().toCharArray()) {
            if (c == 's') count++;
        }
        return count;
    }

    // 2. Top-5 largest files
    private static void printTop5LargestFiles(Path path, BufferedWriter writer) throws IOException {
        writer.write("--- Top 5 Largest Files ---\n");
        try (Stream<Path> stream = Files.walk(path)) {
            stream.filter(Files::isRegularFile)
                    .map(p -> {
                        try { return new AbstractMap.SimpleEntry<>(p, Files.size(p)); }
                        catch (IOException e) { return new AbstractMap.SimpleEntry<>(p, 0L); }
                    })
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .forEach(entry -> {
                        try {
                            writer.write(entry.getKey().toAbsolutePath() + " - " + entry.getValue() + " bytes\n");
                        } catch (IOException ignored) {}
                    });
        }
    }

    // 3. Average file size
    private static void printAverageFileSize(Path path, BufferedWriter writer) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            var stats = stream.filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try { return Files.size(p); }
                        catch (IOException e) { return 0L; }
                    })
                    .summaryStatistics();

            writer.write("Average File Size: " + (stats.getCount() == 0 ? 0 : stats.getAverage()) + " bytes\n");
        }
    }

    // 4. Number of files and folders divided by the first letters
    private static void countByAlphabet(Path path, BufferedWriter writer) throws IOException {
        Map<Character, Integer> fileCounts = new TreeMap<>();
        Map<Character, Integer> folderCounts = new TreeMap<>();

        try (Stream<Path> stream = Files.walk(path)) {
            stream.forEach(p -> {
                String name = p.getFileName().toString();
                if (!name.isEmpty()) {
                    char firstChar = Character.toUpperCase(name.charAt(0));
                    if (Character.isLetter(firstChar)) {
                        if (Files.isDirectory(p)) {
                            folderCounts.put(firstChar, folderCounts.getOrDefault(firstChar, 0) + 1);
                        } else {
                            fileCounts.put(firstChar, fileCounts.getOrDefault(firstChar, 0) + 1);
                        }
                    }
                }
            });
        }

        writer.write("--- Grouped by First Letter ---\n");
        for (char c = 'A'; c <= 'Z'; c++) {
            int files = fileCounts.getOrDefault(c, 0);
            int folders = folderCounts.getOrDefault(c, 0);
            if (files > 0 || folders > 0) {
                writer.write(c + ": " + files + " files, " + folders + " folders\n");
            }
        }
    }
}
