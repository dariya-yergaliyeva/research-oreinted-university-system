package patterns.factory;
import users.Admin;
import users.GraduateStudent;
import users.Student;
import users.Teacher;
import users.User;


public class UniversityUserFactory extends UserFactory {
    @Override
    public User createUser(String type) {
        switch (type) {
            case "STUDENT": return new Student();
            case "TEACHER": return new Teacher();
            case "ADMIN": return new Admin();
            case "GRADUATE": return new GraduateStudent();
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}