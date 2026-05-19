package users;

import courses.Course;
import courses.Mark;
import enums.TeacherPosition;
import enums.UrgencyLevel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import research.ResearcherRole;
public class Teacher extends Employee {
    private TeacherPosition position;
    private List<Course> courses;
    private double rating;
    private ResearcherRole researcherRole;

    public Teacher(String id, String firstName, String lastName, String email, String password, String employeeId, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, employeeId, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.researcherRole = null;
    }

    
    public Teacher() {
        super("0", "", "", "", "", "0", "");
        this.courses = new ArrayList<>();
        this.position = TeacherPosition.LECTOR;
    }

    public void sendComplaint(Student student, UrgencyLevel urgency) {
        System.out.println("Complaint about " + student.getFirstName() +
                " | Urgency: " + urgency);
    }

    public void putMark(Student student, Course course, Mark mark) {
        
        System.out.println("Mark given to " + student.getFirstName() +
                " for " + course.getName() + ": " + mark.getTotalMark());
    }

    public List<Student> viewStudents(Course course) {
        return course.getStudents();
    }

    public void generateMarkReport(Course course) {
        System.out.println("=== Mark Report for " + course.getName() + " ===");
        for (Student s : course.getStudents()) {
            System.out.println(s.getFirstName() + " GPA: " + s.getGpa());
        }
    }

    public void addCourse(Course c) { 
        courses.add(c); 
    }
    public TeacherPosition getPosition() { 
        return position; 
    }
    public double getRating() { 
        return rating; 
    }
    public List<Course> getCourses(){ 
        return courses; 
    }
    public void setRating(double rating) { 
        this.rating = rating;
    }
    public ResearcherRole getResearcherRole(){ 
        return researcherRole; 
    }
    public void setResearcherRole(ResearcherRole r){ 
        this.researcherRole = r; 
    }
    @Override
    public void displayInfo() {
        System.out.println("Teacher: " + getFirstName() + " "
                         + getLastName() + " | Position: " + position
                         + " | Rating: " + rating);
    }

    @Override
    public String toString() {
        return "Teacher{name='" + getFirstName() + "', position=" + position + "}";
    }

    public void generateReport(Course course) {
        if (course == null) {
            System.out.println("Course is null. Report cannot be generated.");
            return;
        }

        System.out.println("\n=== MARK REPORT ===");
        System.out.println("Teacher: " + getFirstName() + " " + getLastName());
        System.out.println("Course: " + course.getName() + " [" + course.getCourseId() + "]");
        System.out.println("Students enrolled: " + course.getStudents().size());

        List<StudentReportRow> rows = new ArrayList<>();
        double courseTotal = 0;
        int markCount = 0;

        for (Student student : course.getStudents()) {
            List<Mark> marks = course.getMarksForStudent(student);

            double studentTotal = 0;
            for (Mark mark : marks) {
                studentTotal += mark.getTotalMark();
            }

            double average = marks.isEmpty() ? 0 : studentTotal / marks.size();
            rows.add(new StudentReportRow(student, average, marks.size()));

            courseTotal += studentTotal;
            markCount += marks.size();
        }

        double courseAverage = markCount == 0 ? 0 : courseTotal / markCount;
        System.out.printf("Average mark: %.2f\n", courseAverage);
        System.out.println("Total marks: " + markCount);

        System.out.println("\n--- Failed students (< 50 or no marks) ---");
        boolean hasFailed = false;
        for (StudentReportRow row : rows) {
            if (row.markCount == 0 || row.average < 50) {
                hasFailed = true;
                System.out.printf("%s %s | avg: %.2f | marks: %d | status: %s\n",
                        row.student.getFirstName(),
                        row.student.getLastName(),
                        row.average,
                        row.markCount,
                        row.markCount == 0 ? "NO MARKS" : "FAILED");
            }
        }
        if (!hasFailed) {
            System.out.println("No failed students.");
        }

        System.out.println("\n--- Top students ---");
        rows.stream()
                .filter(row -> row.markCount > 0)
                .sorted(Comparator.comparingDouble((StudentReportRow row) -> row.average).reversed())
                .limit(3)
                .forEach(row -> System.out.printf("%s %s | avg: %.2f | marks: %d\n",
                        row.student.getFirstName(),
                        row.student.getLastName(),
                        row.average,
                        row.markCount));
    }

    private static class StudentReportRow {
        private Student student;
        private double average;
        private int markCount;

        private StudentReportRow(Student student, double average, int markCount) {
            this.student = student;
            this.average = average;
            this.markCount = markCount;
        }
    }

}