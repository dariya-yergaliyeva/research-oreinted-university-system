package users;

import comunication.News;
import courses.Course;
import enums.ManagerType;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Manager extends Employee implements Serializable {
    private ManagerType managerType;
    public Manager(String id, String firstName, String lastName, String email, String password, String employeeId, String department, ManagerType managerType) {
        super(id, firstName, lastName, email, password, employeeId, department);
        this.managerType = managerType;
    }
    public void assignCourseToTeacher(Course c, Teacher t){
        c.addTeacher(t);
        t.addCourse(c);
        System.out.println("assigned " + t.getFirstName() + " to course " + c.getName());
    }
    public void approveRegistration(Student s, Course c){
        s.registerCourse(c);
        System.out.println("registration approved: " + s.getFirstName() + " -> " + c.getName());
    }
    public void addCourseForRegistration(Course c){
        System.err.println("course opened for registration: " + c.getName());
    }
    public void manageNews(News n) {
        System.out.println("Managing news: " + n.getTitle());
    }
    public List<Student> viewStudentSortedByGPA(List<Student> students){
        System.out.println("=== Students by GPA ===");
        List<Student> sorted = students.stream().sorted().collect(Collectors.toList());
        sorted.forEach(Student::displayInfo);
        return sorted;
    }
    public List<Student> viewStudentsSortedAlphabetically(List<Student> students) {
        System.out.println("=== Students alphabetically ===");
        List<Student> sorted = students.stream()
            .sorted(Comparator.comparing(User::getLastName))
            .collect(Collectors.toList());
        sorted.forEach(Student::displayInfo);
        return sorted;
    }
    @Override
    public void displayInfo() {
        System.out.println("Manager: " + getFirstName() + " "+ getLastName() + " | Type: " + managerType);
    }
 
    public ManagerType getManagerType() { 
        return managerType; 
    }

}