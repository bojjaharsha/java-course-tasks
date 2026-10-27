package com.epam.files.task8;

import java.lang.reflect.Field;
import java.sql.*;

public class JabaORM {

    private final Connection connection;

    public JabaORM(Connection connection) {
        this.connection = connection;
    }

    public void save(Object entity) throws Exception {
        Class<?> clazz = entity.getClass();
        String tableName = clazz.getAnnotation(Table.class).name();

        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                cols.append(field.getAnnotation(Column.class).name()).append(",");
                vals.append("'").append(field.get(entity)).append("',");
            }
        }
        cols.setLength(cols.length() - 1);
        vals.setLength(vals.length() - 1);

        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + vals + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void delete(Object entity) throws Exception {
        Class<?> clazz = entity.getClass();
        String tableName = clazz.getAnnotation(Table.class).name();
        String idColumn = null;
        Object idValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                idColumn = field.getAnnotation(Column.class).name();
                idValue = field.get(entity);
                break;
            }
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = '" + idValue + "'";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
