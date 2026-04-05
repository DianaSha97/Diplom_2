package ru.educationservices.stellarburgers.response.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;
}
