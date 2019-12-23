package BaseClasses;

public class Account {
    private String firstName;
    private String lastName;
    private String username;

    private String position;

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
