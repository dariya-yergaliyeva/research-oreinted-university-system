package users;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Employee implements Serializable{
    private List<String> logs;

    public Admin(String id, String firstName, String lastName, String email, String password, String employeeId, String department) {
        super(id, firstName, lastName, email, password, employeeId, department);
        this.logs = new ArrayList<>();
    }

    public Admin() {
        super("0", "", "", "", "", "0", "");
        this.logs = new ArrayList<>();
    }

    public void addUser(User user) {
        logs.add("Added user: " + user.getFirstName());
        System.out.println("User added: " + user.getFirstName());
    }

    public void removeUser(User user) {
        logs.add("Removed user: " + user.getFirstName());
        System.out.println("User removed: " + user.getFirstName());
    }

    public void updateUser(User user) {
        logs.add("Updated user: " + user.getFirstName());
        System.out.println("User updated: " + user.getFirstName());
    }

    public List<String> viewLogs() {
        System.out.println("=== SYSTEM LOGS ===");
        logs.forEach(System.out::println);
        return logs;
    }
    @Override
    public void displayInfo() {
        System.out.println("Admin: " + getFirstName()
                         + " " + getLastName());
    }
    @Override
    public String toString() {
        return "Admin{name='" + getFirstName() + "'}";
    }
}