package comunication;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import users.User;
 
public class Message implements Serializable {
 
    private User sender;
    private User receiver;
    private String content;
    private Date date;
 
    public Message(User sender, User receiver, String content) {
        this.sender   = sender;
        this.receiver = receiver;
        this.content  = content;
        this.date     = new Date();
    }
 
    public void send() {
        System.out.println("Message from " + sender.getFirstName() + " to " + receiver.getFirstName() + ": " + content);
    }
 
    public User getSender(){ 
        return sender;
    }
    public User getReceiver(){ 
        return receiver; 
    }
    public String getContent() { 
        return content; 
    }
    public Date getDate(){ 
        return date; 
    }
 
    @Override
    public String toString() {
        return "Message"
             + "from=" + sender.getFirstName()
             + ", to=" + receiver.getFirstName()
             + ", content='" + content + '\''
             + ", date=" + date;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message m = (Message) o;
        return Objects.equals(sender, m.sender)
            && Objects.equals(receiver, m.receiver)
            && Objects.equals(date, m.date);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, date);
    }
}