package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Genre;

public class GenreWrapper {
    private int id;
    private String name;

    public GenreWrapper(Genre genre) {
        this.id = genre.getId();
        this.name = genre.name();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
