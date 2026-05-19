package courses;
import enums.CourseType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import users.Student;
import users.Teacher;

public class Course implements Serializable{
    private String courseId;
    private String name;
    private int credits;
    private CourseType courseType;
    private List<Teacher> teachers;
    private List<Student> students;
    private List<Lesson> lessons;
    private int maxStudents;
    private Map<Student, List<Mark>> markMap;

    public Course(String courseId, String name, int credits, CourseType courseType, int maxStudents){
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.maxStudents = maxStudents;
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.markMap = new HashMap<>();
    }
    public void addTeacher(Teacher t){
        teachers.add(t);
        System.out.println("Teacher " + t.getFirstName() + " added to course " + name);
    }
    public void addStudent(Student s){
        if(students.size() >= maxStudents){
            System.out.println("Course " + name + " is full");
            return;
        }
        students.add(s);
        markMap.put(s, new ArrayList<>());
        System.out.println("Student " + s.getFirstName() + " enrolled in " + name);
    }
    public void addLesson(Lesson l){
        lessons.add(l);
    }
    public void addMark(Student s, Mark m) {
        markMap.computeIfAbsent(s, k -> new ArrayList<>()).add(m);
    }
    public List<Mark> getMarksForStudent(Student s) {
        return markMap.getOrDefault(s, new ArrayList<>());
    }
    public void removeStudent(Student s){
        students.remove(s);
        markMap.remove(s);
    }
    public String getCourseId(){
        return courseId;
    }
    public String getName(){
        return name;
    }
    public int getCredits(){
        return credits;
    }
    public CourseType getCourseType(){
        return courseType;
    }
    public List<Teacher> getTeachers(){
        return teachers;
    }
    public List<Student> getStudents(){
        return students;
    }
    public List<Lesson> getLesson(){
        return lessons;
    }
    public int getMaxStudents(){
        return maxStudents;
    }
    @Override
    public String toString(){
        return "Course{"
             + "id='" + courseId + '\''
             + ", name='" + name + '\''
             + ", credits=" + credits
             + ", type=" + courseType
             + ", students=" + students.size()
             + '}';
    }
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof Course)){
            return false;
        }
        Course c = (Course) o;
        return Objects.equals(courseId, c.courseId);
    }
    @Override 
    public int hashCode(){
        return Objects.hash(courseId);
    }
}