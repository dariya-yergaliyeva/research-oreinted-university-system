package courses;

import enums.LessonType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import users.Student;
import users.Teacher;


public class Lesson implements Serializable {
    private String lessonId;
    private LessonType lessonType;
    private Date date;
    private String room;
    private Teacher teacher;
    private List<Attendance> attendanceList;
    public Lesson(String lessonId, LessonType lessonType, Date date, String room, Teacher teacher){
        this.lessonId = lessonId;
        this.lessonType = lessonType;
        this.date = date;
        this.room = room;
        this.teacher = teacher;
        this.attendanceList = new ArrayList<>();
    }
    public List<Attendance> getAttendance(){
        return attendanceList;
    }
    public void markAttendance(Student student, boolean present){
        attendanceList.removeIf(a -> a.getStudent().equals(student));
        Attendance a = new Attendance(student, this, present);
        attendanceList.add(a);
        System.out.println(student.getFirstName() + (present ? " PRESENT" : " ABSENT"));
    }
    public List<Attendance> getAttendanceForStudent(Student s) {
        return attendanceList.stream()
            .filter(a -> a.getStudent().equals(s))
            .collect(Collectors.toList());
    }
 
    public String getLessonId(){
        return lessonId;
    }
    public LessonType getLessonType(){
        return lessonType;
    }
    public Date getDate(){
        return date;
    }
    public String getRoom(){
        return room;
    }
    public Teacher getTeacher(){
        return teacher;
    }
    @Override
    public String toString(){
        return "Lesson{"
             + "id='" + lessonId + '\''
             + ", type=" + lessonType
             + ", room='" + room + '\''
             + ", date=" + date
             + '}';
    }
}