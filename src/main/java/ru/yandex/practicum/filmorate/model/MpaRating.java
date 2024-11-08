package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

public enum MpaRating {
    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    private final String name;

    MpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
    @JsonValue
    public int getId() {
        return id;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public static MpaRating fromId(int id) {
        for (MpaRating rating : values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new NotFoundException("MPA с таким id отсутствует: " + id);
    }

    @JsonCreator
    public static MpaRating fromJson(@JsonProperty("id") int id) {
        for (MpaRating rating : values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new NotFoundException("MPA с таким id отсутствует: " + id);
    }
}

