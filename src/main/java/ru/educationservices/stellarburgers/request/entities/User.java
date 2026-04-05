package ru.educationservices.stellarburgers.request.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Генерирует конструктор со всеми полями: email, password, name
@NoArgsConstructor  // Генерирует пустой конструктор
public class User {
    private String email;
    private String password;
    private String name;

    // Дополнительный конструктор только для email и password
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
