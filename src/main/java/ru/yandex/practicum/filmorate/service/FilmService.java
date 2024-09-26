package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public void addLike(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        User user = findUserById(userId);

        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        User user = findUserById(userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                          .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                          .limit(count)
                          .collect(Collectors.toList());
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

    private Film findFilmById(Long id) {
        Film film = filmStorage.findByIdFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return film;
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
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("Длительность фильма должна быть положительным числом");
        }
    }
}

