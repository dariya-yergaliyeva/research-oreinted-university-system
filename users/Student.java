package users;
import courses.Attendance;
import courses.Course;
import courses.Lesson;
import courses.Mark;
import java.util.ArrayList;
import java.util.List;
import research.ResearcherRole;
import university.StudentOrganization;

public class Student extends User implements Comparable<Student> {
    private String studentId;
    private double gpa;
    private int credits;
    private int failCount;
    private List<Course> courses;
    private List<StudentOrganization> organizations;
    private ResearcherRole researcherRole;

    public Student(String id, String firstName, String lastName, String email, String password, String studentId) {
        super(id, firstName, lastName, email, password);
        this.studentId = studentId;
        this.gpa = 0.0;
        this.credits = 0;
        this.failCount = 0;
        this.courses = new ArrayList<>();
        this.organizations = new ArrayList<>();
        this.researcherRole = null;
    }

    public Student() {
        super("0", "", "", "", "");
        this.courses = new ArrayList<>();
        this.organizations = new ArrayList<>();
    }

    public void registerCourse(Course course) {
        if (credits + course.getCredits() > 21) {
            System.out.println("ERROR: Credit limit exceeded. Max 21 credits");
            return;
        }
        courses.add(course);
        course.addStudent(this);
        credits += course.getCredits();
        System.out.println(getFirstName() + " registered for " + course.getName());
    }

    public List<Mark> viewMarks() {
        List<Mark> marks = new ArrayList<>();
        for (Course c : courses) {
            marks.addAll(c.getMarksForStudent(this));
        }
        return marks;
    }

    public void getTranscript() {
        System.out.println("=== Transcript for " + getFirstName() + " ===");
        System.out.println("GPA: " + gpa);
        System.out.println("Credits: " + credits);
        for (Course c : courses) {
            System.out.println("  Course: " + c.getName() + " (" + c.getCredits() + " cr)");
        }
    }

    public void rateTeacher(Teacher teacher, int rating) {
        teacher.setRating(rating);
        System.out.println(getFirstName() + " rated " + teacher.getFirstName() + ": " + rating);
    }

    public List<Attendance> viewAttendance() {
        List<Attendance> result = new ArrayList<>();
        for (Course c : courses) {
            for (Lesson l : c.getLesson()) {
                result.addAll(l.getAttendanceForStudent(this));
            }
        }
        return result;
    }
    public void addOrganization(StudentOrganization o) {
        organizations.add(o);
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa); 
    }

    public String getStudentId() { 
        return studentId; 
    }
    public double getGpa() { 
        return gpa; 
    }
    public void setGpa(double gpa) { 
        this.gpa = gpa; 
    }
    public int getCredits() { 
        return credits; 
    }
    public int getFailCount() { 
        return failCount; 
    }
    public void incrementFailCount() { 
        this.failCount++; 
    }
    public List<Course> getCourses() { 
        return courses; 
    }
    public List<StudentOrganization> getOrganizations(){ 
        return organizations; 
    }
    public ResearcherRole getResearcherRole(){ 
        return researcherRole; 
    }
    public void setResearcherRole(ResearcherRole r){ 
        this.researcherRole = r; 
    }
    @Override
    public void displayInfo() {
        System.out.println("Student: " + getFirstName() + " "
                         + getLastName() + " | GPA: " + gpa
                         + " | Credits: " + credits);
    }
    @Override
    public String toString() {
        return "Student{name='" + getFirstName() + "', gpa=" + gpa + ", credits=" + credits + "}";
    }
}