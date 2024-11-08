package ru.yandex.practicum.filmorate.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class FilmResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private MpaRatingResponse mpa;
    private List<GenreResponse> genres;
    private Set<Long> likes;

    public FilmResponse(Long id, String name, String description, LocalDate releaseDate, int duration, MpaRatingResponse mpa,
                        List<GenreResponse> genres, Set<Long> likes) {
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

    public MpaRatingResponse getMpa() {
        return mpa;
    }

    public List<GenreResponse> getGenres() {
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

    public void setMpa(MpaRatingResponse mpa) {
        this.mpa = mpa;
    }

    public void setGenres(List<GenreResponse> genres) {
        this.genres = genres;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }
}
