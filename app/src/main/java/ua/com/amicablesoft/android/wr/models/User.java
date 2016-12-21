package ua.com.amicablesoft.android.wr.models;

/**
 * Created by olha on 12/18/16.
 */

public class User {
    private String id;
    private String email;

    public User() {
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
