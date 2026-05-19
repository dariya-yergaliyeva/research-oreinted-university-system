package users;
import enums.Language;
import comunication.Message;
import patterns.observer.Observer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Observer, Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Language language;
    private List<Message> inbox = new ArrayList<>();
    private List<Message> sent = new ArrayList<>();

    public User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.language = Language.EN;
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void logout() {
        System.out.println(firstName + " logged out.");
    }

    public void sendMessage(User to, String text) {
        if (sent == null) sent = new ArrayList<>();
        Message m = new Message(this, to, text);
        sent.add(m);
        to.receiveMessage(m);
        System.out.println("Message sent to " + to.firstName + ".");
    }

    public void receiveMessage(Message m) {
        if (inbox == null) inbox = new ArrayList<>();
        inbox.add(m);
    }

    public List<Message> getInbox() {
        if (inbox == null) inbox = new ArrayList<>();
        return inbox;
    }

    public List<Message> getSent() {
        if (sent == null) sent = new ArrayList<>();
        return sent;
    }

    public void update(String message) {
        System.out.println("Notification for " + firstName + ": " + message);
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
    public abstract void displayInfo();
    public String getId() { 
        return id; 
    }
    public String getFirstName() { 
        return firstName; 
    }
    public String getLastName() { 
        return lastName; 
    }
    public String getEmail() { 
        return email; 
    }
    public Language getLanguage() { 
        return language; 
    }
    public void setEmail(String email){ 
        this.email = email; 
    }
    public void setPassword(String password){ 
        this.password = password; 
    }

    @Override
    public String toString() {
        return "User id='" + id + "', name='" + firstName + " " + lastName + "', email='" + email ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}