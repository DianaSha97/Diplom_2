package ru.educationservices.stellarburgers.responseEntities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ingredient {
    private String _id;

    public <E> E get_id() {
        return (E) _id;
    }
}
