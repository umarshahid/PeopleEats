package user;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private UserType type;
    private double creditBalance;
    private List<String> feedbacks;

    public User(String username, String password, UserType type) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.creditBalance = 0.0;
        this.feedbacks = new ArrayList<>();
    }

    // Method to switch user mode
    public void switchMode(UserType newType) {
        this.type = newType;
    }

    public String getUsername() {
        return username;
    }
}
