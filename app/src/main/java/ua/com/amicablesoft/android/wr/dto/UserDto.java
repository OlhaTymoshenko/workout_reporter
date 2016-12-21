package ua.com.amicablesoft.android.wr.dto;

/**
 * Created by olha on 12/18/16.
 */

public class UserDto {
    private String email;

    public UserDto() {
    }

    public UserDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
