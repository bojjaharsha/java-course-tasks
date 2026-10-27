package com.epam.files.task4;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBUnitTest {

    private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (ID INT PRIMARY KEY, USERNAME VARCHAR(50), EMAIL VARCHAR(50))");
            stmt.execute("DELETE FROM USERS");
            stmt.execute("INSERT INTO USERS VALUES (1, 'Alice', 'alice@example.com')");
            stmt.execute("INSERT INTO USERS VALUES (2, 'Bob', 'bob@example.com')");
            stmt.execute("INSERT INTO USERS VALUES (3, 'Charlie', 'charlie@example.com')");
        }
    }

    // 1. verifying row count
    @Test
    void testUserCount() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS")) {
            assertTrue(rs.next());
            assertEquals(3, rs.getInt(1));
        }
    }

    // 2. verifying single record retrieval
    @Test
    void testFindUserById() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             PreparedStatement ps = conn.prepareStatement("SELECT USERNAME FROM USERS WHERE ID = ?")) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Alice", rs.getString("USERNAME"));
            }
        }
    }

    // 3. insert
    @Test
    void testInsertUser() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             PreparedStatement ps = conn.prepareStatement("INSERT INTO USERS VALUES (4, 'David', 'david@example.com')")) {
            int inserted = ps.executeUpdate();
            assertEquals(1, inserted);
        }
    }

    // 4. update
    @Test
    void testUpdateUser() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET USERNAME = 'Bob-Updated' WHERE ID = ?")) {
            ps.setInt(1, 2);
            assertEquals(1, ps.executeUpdate());
        }
    }

    // 5. delete
    @Test
    void testDeleteUser() throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             PreparedStatement ps = conn.prepareStatement("DELETE FROM USERS WHERE ID = ?")) {
            ps.setInt(1, 3);
            assertEquals(1, ps.executeUpdate());
        }
    }
}
