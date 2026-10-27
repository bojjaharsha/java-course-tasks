package com.epam.files.task1;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    @Test
    void testSerializationExcludesNumericalFields() throws Exception {
        String testFile = "target/manager.ser";
        new File("target").mkdirs();

        Manager original = new Manager("Alice", "Engineering", 101, 75000.0, "Core-Team", 8);

        // 1. Serialize
        SerializationUtil.serialize(original, testFile);

        // 2. Deserialize
        Manager restored = SerializationUtil.deserialize(testFile);

        // Strings are preserved
        assertEquals("Alice", restored.getName());
        assertEquals("Engineering", restored.getDepartment());
        assertEquals("Core-Team", restored.getTeamName());

        // Numerical fields were NOT serialized (transient -> defaults to 0)
        assertEquals(0, restored.getEmployeeId(), "employeeId should not be serialized");
        assertEquals(0.0, restored.getSalary(), "salary should not be serialized");
        assertEquals(0, restored.getTeamSize(), "teamSize should not be serialized");
    }
}
