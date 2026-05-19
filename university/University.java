package university;

import comunication.News;
import courses.Course;
import research.Researcher;
import research.ResearchJournal;
import research.ResearcherRole;
import users.User;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
 

public class University implements Serializable {
 
    private static final long serialVersionUID = 1L;
 
    private static University instance;
 
    private String name;
    private List<User> users;
    private List<Course> courses;
    private List<News> newsList;
    private List<ResearchJournal> journals;
    private List<String> logs;
 
    private University() {
        this.name = "University";
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.newsList = new ArrayList<>();
        this.journals = new ArrayList<>();
        this.logs = new ArrayList<>();
    }
 
    public static University getInstance() {
        if (instance == null) {
            instance = new University();
        }
        return instance;
    }
 
 
    public void addUser(User user) {
        users.add(user);
        log("User added: " + user.getFirstName() + " " + user.getLastName());
    }
 
    public List<User> getUsers() {
        return users;
    }

    public Researcher getTopCitedResearcher() {
        return users.stream()
            .filter(u -> {
                if (u instanceof users.Teacher) {
                    return ((users.Teacher) u).getResearcherRole() != null;
                }
                if (u instanceof users.Student) {
                    return ((users.Student) u).getResearcherRole() != null;
                }
                return false;
            })
            .map(u -> {
                if (u instanceof users.Teacher)
                    return ((users.Teacher) u).getResearcherRole();
                return ((users.Student) u).getResearcherRole();
            })
            .max(Comparator.comparingInt(ResearcherRole::calculateHIndex))
            .orElse(null);
    }

    public void removeUser(User user) {
        users.remove(user);
        log("User removed: " + user.getFirstName() + " " + user.getLastName());
    }
 
    public Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }
 
    public void addCourse(Course c){ 
        courses.add(c);
    }
    public void addNews(News n){ 
        newsList.add(n); 
    }
    public void addJournal(ResearchJournal j){ 
        journals.add(j); 
    }
 
    public List<Course> getCourses(){ 
        return courses; 
    }
    public List<News> getNewsList(){ 
        return newsList; 
    }
    public List<ResearchJournal> getJournals(){ 
        return journals; 
    }
    public List<String> getLogs(){ 
        return logs; 
    }
    public String getName(){ 
        return name; 
    }
 
    public void log(String action) {
        String entry = new java.util.Date() + " | " + action;
        logs.add(entry);
    }

    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
            log("Data saved to: " + filename);
            System.out.println("saved successfully.");
        } catch (IOException e) {
            System.out.println("save error: " + e.getMessage());
        }
    }
 
    public static University loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            instance = (University) ois.readObject();
            System.out.println("data loaded from: " + filename);
        } catch (Exception e) {
            System.out.println("no save found — starting fresh.");
            instance = new University();
        }
        return instance;
    }
}