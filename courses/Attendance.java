package courses;

import java.io.Serializable;
import java.util.Date;
import users.Student;


public class Attendance implements Serializable{
    private Student student;
    private Lesson lesson;
    private boolean isPresent;
    private Date date;

    public Attendance(Student student, Lesson lesson, boolean isPresent){
        this.student = student;
        this.lesson = lesson;
        this.isPresent = isPresent;
        this.date = new Date();
    }
    public void markPresent(){
        this.isPresent = true;
        System.out.println(student.getFirstName() + " marked PRESENT");
    }
    public void markAbsent(){
        this.isPresent = false;
        System.out.println(student.getFirstName() + " marked ABSENT");
    }
    public Student getStudent() { 
        return student;
    }
    public Lesson getLesson() { 
        return lesson; 
    }
    public boolean isPresent(){ 
        return isPresent; 
    }
    public Date getDate(){ 
        return date;
    }
 
    @Override
    public String toString() {
        return "Attendance{"
             + "student=" + student.getFirstName() + " " + student.getLastName()
             + ", present=" + isPresent
             + ", date=" + date
             + '}';
    }
}