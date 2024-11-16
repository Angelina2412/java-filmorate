package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        FilmResponse film = findFilmById(filmId);
        User user = findUserById(userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Film updateFilm(Long id, Film film) {
        if (id == null) {
            throw new IllegalArgumentException("ID фильма не может быть пустым");
        }

        Film existingFilm = filmStorage.findByIdFilm(id);
        if (existingFilm == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        validateFilm(film);
        film.setId(id);
        return filmStorage.update(film);
    }

    public boolean exists(Long filmId) {
        return filmStorage.findByIdFilm(filmId) != null;
    }

    public FilmResponse findFilmById(Long id) {
        Film film = filmStorage.findByIdFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        MpaRating mpaRating = film.getMpaRating();
        List<Genre> genres = new ArrayList<>(film.getGenres());

        return new FilmResponse(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                                film.getDuration(), mpaRating, genres, film.getLikes());
    }

    private User findUserById(Long userId) {
        User user = userStorage.findByIdUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new IllegalArgumentException("Название фильма не может быть пустым");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Некорректная дата выхода фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1920, 1, 1))) {
            throw new IllegalArgumentException("Дата выхода фильма не может быть раньше 1920 года");
        }
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("Длительность фильма должна быть положительным числом");
        }
        try {
            filmStorage.findMpaRatingById(film.getMpaRating().getId());
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("Указан некорректный рейтинг MPA с id = " + film.getMpaRating().getId());
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    filmStorage.findGenreById(genre.getId());
                } catch (NotFoundException e) {
                    throw new IllegalArgumentException("Указан некорректный жанр с id = " + genre.getId());
                }
            }
        }
    }

    public List<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return filmStorage.findGenreById(id);
    }

    public List<MpaRating> findAllMpaRatings() {
        return filmStorage.findAllMpaRatings();
    }

    public MpaRating findMpaRatingById(int id) {
        MpaRating mpaRating = filmStorage.findMpaRatingById(id);
        if (mpaRating == null) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
        return mpaRating;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findByIdFilm(filmId);
        User user = userStorage.findByIdUser(userId);

        if (film != null && user != null) {
            filmStorage.addLike(filmId, userId);
        } else {
            throw new NotFoundException("Фильм или пользователь не найден");
        }
    }
}


