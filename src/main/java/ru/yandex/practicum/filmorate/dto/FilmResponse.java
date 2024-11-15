package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class FilmResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private MpaRating mpa;
    private List<Genre> genres;
    private Set<Long> likes;

    public FilmResponse(Long id, String name, String description, LocalDate releaseDate, int duration, MpaRating mpa,
                        List<Genre> genres, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
        this.likes = likes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public MpaRating getMpa() {
        return mpa;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Set<Long> getLikes() {
        return likes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMpa(MpaRating mpa) {
        this.mpa = mpa;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }
}

