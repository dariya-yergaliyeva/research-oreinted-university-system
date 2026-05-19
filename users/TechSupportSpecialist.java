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
        allRequests.add(r);
        System.out.println("new request received");
    }


    public List<TechRequest> viewNewRequests() {
        List<TechRequest> pending = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.NEW || r.getStatus() == RequestStatus.VIEWED)
                .collect(Collectors.toList());
        pending.stream().filter(r -> r.getStatus() == RequestStatus.NEW).forEach(TechRequest::markViewed);
        return pending;
    }

    public void acceptRequest(TechRequest r) {
        if (r.getStatus() == RequestStatus.REJECTED || r.getStatus() == RequestStatus.DONE) {
            System.out.println("Cannot accept: request is already " + r.getStatus());
            return;
        }
        r.accept();
    }

    public void rejectRequest(TechRequest r) {
        if (r.getStatus() == RequestStatus.DONE) {
            System.out.println("Cannot reject: request is already DONE");
            return;
        }
        r.reject();
    }

    public void markDone(TechRequest r) {
        if (r.getStatus() == RequestStatus.REJECTED) {
            System.out.println("Cannot mark DONE: request was REJECTED");
            return;
        }
        if (r.getStatus() != RequestStatus.ACCEPTED) {
            System.out.println("Cannot mark DONE: request must be ACCEPTED first (current: " + r.getStatus() + ")");
            return;
        }
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