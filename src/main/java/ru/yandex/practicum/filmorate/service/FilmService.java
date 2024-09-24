package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findByIdFilm(filmId);
        User user = userStorage.findByIdUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findByIdFilm(filmId);
        User user = userStorage.findByIdUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                          .sorted(Comparator.comparingInt(film -> -film.getLikes().size())) // сортировка по убыванию количества лайков
                          .limit(count)
                          .collect(Collectors.toList());
    }
}
