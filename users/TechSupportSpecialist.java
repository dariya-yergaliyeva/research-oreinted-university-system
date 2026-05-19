package users;

import courses.TechRequest;
import enums.RequestStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
public class TechSupportSpecialist extends Employee implements Serializable {
 
    private List<TechRequest> allRequests;
 
    public TechSupportSpecialist(String id, String firstName, String lastName, String email, String password, String employeeId, String department) {
        super(id, firstName, lastName, email, password, employeeId, department);
        this.allRequests = new ArrayList<>();
    }
 
    public void receiveRequest(TechRequest r) {
        r.markViewed(); 
        allRequests.add(r);
        System.out.println("new request received and viewed");
    }
 

    public List<TechRequest> viewNewRequests() {
        return allRequests.stream().filter(r -> r.getStatus() == RequestStatus.NEW || r.getStatus() == RequestStatus.VIEWED).collect(Collectors.toList());
    }
 
    public void acceptRequest(TechRequest r) {
        r.accept();
    }
 
    public void rejectRequest(TechRequest r) {
        r.reject();
    }
 
    public void markDone(TechRequest r) {
        r.markDone();
    }
 
    @Override
    public void displayInfo() {
        System.out.println("TechSupport: " + getFirstName() + " "
                         + getLastName()
                         + " | Requests: " + allRequests.size());
    }
 
    public List<TechRequest> getAllRequests() { return allRequests; }
}