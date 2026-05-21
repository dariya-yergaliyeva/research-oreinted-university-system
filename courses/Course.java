package courses;
import enums.CourseType;
import enums.LessonType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Map<String, List<Mark>> markMap;

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
        if(!students.contains(s)){
            if(students.size() >= maxStudents){
                System.out.println("Course " + name + " is full");
                return;
            }
            students.add(s);
            markMap.computeIfAbsent(s.getEmail(), k -> new ArrayList<>());
            System.out.println("Student " + s.getFirstName() + " enrolled in " + name);
        }
        if(!s.getCourses().contains(this)) s.getCourses().add(this);
    }
    public void addLesson(Lesson l){
        lessons.add(l);
    }
    public void addMark(Student s, Mark m) {
        markMap.computeIfAbsent(s.getEmail(), k -> new ArrayList<>()).add(m);
    }
    public List<Mark> getMarksForStudent(Student s) {
        return markMap.getOrDefault(s.getEmail(), new ArrayList<>());
    }

    private Lesson getOrCreateLesson(String dateKey, Teacher teacher) {
        String id = courseId + "@" + dateKey;
        for (Lesson l : lessons) {
            if (l.getLessonId().equals(id)) return l;
        }
        Date date;
        try { date = new SimpleDateFormat("yyyy-MM-dd").parse(dateKey); }
        catch (Exception e) { date = new Date(); }
        Lesson lesson = new Lesson(id, LessonType.LECTURE, date,
            teacher != null ? teacher.getFirstName() : "—", teacher);
        lessons.add(lesson);
        return lesson;
    }

    public void markAttendance(Student s, String dateKey, boolean present, Teacher teacher) {
        Lesson lesson = getOrCreateLesson(dateKey, teacher);
        lesson.markAttendance(s, present);
    }

    public double getAttendancePercentage(Student s) {
        int total = 0, present = 0;
        for (Lesson l : lessons) {
            for (Attendance a : l.getAttendanceForStudent(s)) {
                total++;
                if (a.isPresent()) present++;
            }
        }
        if (total == 0) return 100.0;
        return present * 100.0 / total;
    }

    public int getPresentCount(Student s) {
        int present = 0;
        for (Lesson l : lessons) {
            for (Attendance a : l.getAttendanceForStudent(s)) {
                if (a.isPresent()) present++;
            }
        }
        return present;
    }

    public int getSessionCount(Student s) {
        int total = 0;
        for (Lesson l : lessons) {
            total += l.getAttendanceForStudent(s).size();
        }
        return total;
    }

    public boolean isAdmittedToFinal(Student s) {
        List<Mark> ms = getMarksForStudent(s);
        double att = 0;
        if (!ms.isEmpty()) {
            Mark m = ms.get(ms.size() - 1);
            att = m.getFirstAttestation() + m.getSecondAttestation();
        }
        return att >= 30 && getAttendancePercentage(s) >= 30;
    }
    public void removeStudent(Student s){
        students.remove(s);
        markMap.remove(s.getEmail());
        s.getCourses().remove(this);
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