package courses;

import users.Student;
import users.Teacher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Schedule implements Serializable{
    private String scheduleId;
    private String semester;
    private List<Lesson> lessons;
    public Schedule(String scheduleId, String semester){
        this.scheduleId = scheduleId;
        this.semester = semester;
        this.lessons = new ArrayList<>();
    }
    public void addLesson(Lesson l){
        lessons.add(l);
        System.out.println("lesson added to schedule: " + l);
    }
    public void removeLesson(Lesson l){
        lessons.remove(l);
        System.out.println("lesson removed from schedule");
    }
    public List<Lesson> getLessonsForStudent(Student s){
        return lessons.stream().filter(l -> l.getAttendance().stream().anyMatch(a -> a.getStudent().equals(s))).collect(Collectors.toList());
    }
    public List<Lesson> getLessonsForTeacher(Teacher t) {
        return lessons.stream().filter(l -> l.getTeacher().equals(t)).collect(Collectors.toList());
    }
    public void generateSchedule(){
        System.out.println("=== generating schedule for: " + semester + " ===");
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        int i = 0;
        for (Lesson l : lessons){
            System.out.println(days[i % days.length] + " -> " + l);
            i++;
        }
    }
    public void printSchedule(){
        System.out.println("=== SCHEDULE [" + semester + "] ===");
        if (lessons.isEmpty()){
            System.out.println("no lessons.");
        } else{
            lessons.forEach(System.out::println);
        }
    }
    public String getScheduleId(){ 
        return scheduleId; 
    }
    public String getSemester(){ 
        return semester; 
    }
    public List<Lesson> getLessons(){ 
        return lessons; 
    }
 
    @Override
    public String toString() {
        return "Schedule{"
             + "id='" + scheduleId + '\''
             + ", semester='" + semester + '\''
             + ", lessons=" + lessons.size()
             + '}';
    }
}