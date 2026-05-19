package courses;

import enums.RequestStatus;
import users.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


public class TechRequest implements Serializable{
    private String description;
    private RequestStatus status;
    private User createdBy;
    private Date date;
    public TechRequest(String description, User createdBy){
        this.description = description;
        this.createdBy = createdBy;
        this. status = RequestStatus.NEW;
        this.date = new Date();
    }

    public void updateStatus(RequestStatus s) {
        this.status = s;
        System.out.println("Request status updated to: " + s);
    }
    public void markViewed(){ 
        updateStatus(RequestStatus.VIEWED); 
    }
    public void accept(){ 
        updateStatus(RequestStatus.ACCEPTED); 
    }
    public void reject(){ 
        updateStatus(RequestStatus.REJECTED); 
    }
    public void markDone(){ 
        updateStatus(RequestStatus.DONE); 
    }
    public String getDescription() { 
        return description; 
    }
    public RequestStatus getStatus(){ 
        return status; 
    }
    public User getCreatedBy(){ 
        return createdBy; 
    }
    public Date getDate(){ 
        return date;
    }
    @Override
    public String toString() {
        return "TechRequest{"
             + "description='" + description + '\''
             + ", status=" + status
             + ", createdBy=" + createdBy.getFirstName()
             + ", date=" + date
             + '}';
    }
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof TechRequest)){
            return false;
        }
        TechRequest r = (TechRequest) o;
        return Objects.equals(description, r.description) && Objects.equals(date, r.date);
    }
    @Override
    public int hashCode(){
        return Objects.hash(description, date);
    }
}