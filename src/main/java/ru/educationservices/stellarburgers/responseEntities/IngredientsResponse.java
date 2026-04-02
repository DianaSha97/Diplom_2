package ru.educationservices.stellarburgers.responseEntities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;

    public List<Ingredient> getData() {
        return data;
    }
}
