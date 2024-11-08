package ru.yandex.practicum.filmorate.dto;

public class GenreResponse {
    private int id;
    private String name;

    public GenreResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

