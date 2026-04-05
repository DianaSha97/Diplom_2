package ru.educationservices.stellarburgers.response.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.educationservices.stellarburgers.request.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
    private String message;
}
