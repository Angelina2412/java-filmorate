package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findByIdFilm(Long id);

    void deleteFilm(Long id);
    List<Genre> findAllGenres();
    Genre findGenreById(int id);
    List<MpaRating> findAllMpaRatings();

    MpaRating findMpaRatingById(int id);

    List<Film> getMostPopularFilms(int count);

    void addLike(Long filmId, Long userId);

}