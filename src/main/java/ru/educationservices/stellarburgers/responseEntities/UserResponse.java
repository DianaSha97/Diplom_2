package ru.educationservices.stellarburgers.responseEntities;

import lombok.Getter;
import lombok.Setter;
import ru.educationservices.stellarburgers.requestEntities.User;

@Getter
@Setter
public class UserResponse {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public Object getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
