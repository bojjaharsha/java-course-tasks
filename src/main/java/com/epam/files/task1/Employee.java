package com.epam.files.task1;

import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String department;

    // Numerical field: must NOT be serialized -> use 'transient'
    private transient int employeeId;
    private transient double salary;

    public Employee(String name, String department, int employeeId, double salary) {
        this.name = name;
        this.department = department;
        this.employeeId = employeeId;
        this.salary = salary;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getEmployeeId() { return employeeId; }
    public double getSalary() { return salary; }
}
