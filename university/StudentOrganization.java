package university;

import users.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public class StudentOrganization implements Serializable {
 
    private String name;
    private List<Student> members;
    private Student head;
 
    public StudentOrganization(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.head = null;
    }
 
    public void addMember(Student s) {
        members.add(s);
        s.addOrganization(this);
        System.out.println(s.getFirstName() + " joined " + name);
    }
 
    public void setHead(Student s) {
        if (!members.contains(s)) {
            addMember(s);
        }
        this.head = s;
        System.out.println(s.getFirstName() + " is now head of " + name);
    }
 
    public void removeMember(Student s) {
        members.remove(s);
        if (s.equals(head)) head = null;
        System.out.println(s.getFirstName() + " left " + name);
    }
 
    public String getName(){ 
        return name; 
    }
    public List<Student> getMembers(){ 
        return members;
    }
    public Student getHead(){ 
        return head;
    }
 
    @Override
    public String toString() {
        return "StudentOrganization{"
             + "name='" + name + '\''
             + ", members=" + members.size()
             + ", head=" + (head != null ? head.getFirstName() : "none")
             + '}';
    }
}