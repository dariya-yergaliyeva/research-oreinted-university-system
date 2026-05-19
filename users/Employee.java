package users;

import java.io.Serializable;

public abstract class Employee extends User implements Serializable {
    private String employeeId;
    private String department;

    public Employee(String id, String firstName, String lastName, String email, String password, String employeeId, String department) {
        super(id, firstName, lastName, email, password);
        this.employeeId = employeeId;
        this.department = department;
    }

    public String getEmployeeId() { 
        return employeeId; 
    }
    public String getDepartment() { 
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}