package com.epam.files.task1;

public class Manager extends Employee {
    private static final long serialVersionUID = 1L;

    private String teamName;
    
    // Numerical field in subclass: must NOT be serialized -> use 'transient'
    private transient int teamSize;

    public Manager(String name, String department, int employeeId, double salary, String teamName, int teamSize) {
        super(name, department, employeeId, salary);
        this.teamName = teamName;
        this.teamSize = teamSize;
    }

    public String getTeamName() { return teamName; }
    public int getTeamSize() { return teamSize; }
}
