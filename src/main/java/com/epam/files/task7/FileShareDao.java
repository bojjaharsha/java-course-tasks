package com.epam.files.task7;

import java.io.*;
import java.sql.*;

public class FileShareDao {

    private final String jdbcUrl;

    public FileShareDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS FILES (ID INT AUTO_INCREMENT PRIMARY KEY, FILE_NAME VARCHAR(255), FILE_DATA BLOB)");
        } catch (SQLException e) {
            throw new RuntimeException("DB Init failed", e);
        }
    }

    // 1. Save file to DB (Streams directly to BLOB)
    public int saveFile(String fileName, File file) throws IOException, SQLException {
        String sql = "INSERT INTO FILES (FILE_NAME, FILE_DATA) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             FileInputStream fis = new FileInputStream(file)) {

            ps.setString(1, fileName);
            ps.setBinaryStream(2, fis, file.length());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // 2. Retrieve file from DB (Streams directly to destination file)
    public void retrieveFile(int fileId, File destination) throws SQLException, IOException {
        String sql = "SELECT FILE_DATA FROM FILES WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fileId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (InputStream is = rs.getBinaryStream("FILE_DATA");
                         FileOutputStream fos = new FileOutputStream(destination)) {
                        is.transferTo(fos);
                    }
                } else {
                    throw new FileNotFoundException("File not found for ID: " + fileId);
                }
            }
        }
    }
}
