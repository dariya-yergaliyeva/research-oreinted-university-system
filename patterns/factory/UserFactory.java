package patterns.factory;
import users.User;
public abstract class UserFactory {
    public abstract User createUser(String type);
}