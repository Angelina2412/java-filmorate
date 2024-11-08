package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

public enum Genre {
    Комедия(1),
    Драма(2),
    Мультфильм(3),
    Триллер(4),
    Документальный(5),
    Боевик(6);

    private final int id;

    Genre(int id) {
        this.id = id;
    }

    @JsonValue
    public int getId() {
        return id;
    }

    public static Genre fromId(int id) {
        for (Genre genre : values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        throw new NotFoundException("Жанр с таким id отсутствует: " + id);
    }

    @JsonCreator
    public static Genre fromJson(@JsonProperty("id") int id) {
        for (Genre genre : values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        throw new NotFoundException("Жанр с таким id отсутствует: " + id);
    }
}

