package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmResponse;
import ru.yandex.practicum.filmorate.dto.GenreResponse;
import ru.yandex.practicum.filmorate.dto.MpaRatingResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Arrays;
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
        FilmResponse film = findFilmById(filmId);
        User user = findUserById(userId);

        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        FilmResponse film = findFilmById(filmId);
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

    public FilmResponse findFilmById(Long id) {
        Film film = filmStorage.findByIdFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        MpaRatingResponse mpa = new MpaRatingResponse(film.getMpaRating().getId(), film.getMpaRating().getName());
        List<GenreResponse> genres = film.getGenres().stream()
                                         .map(genre -> new GenreResponse(genre.getId(), genre.name()))
                                         .collect(Collectors.toList());

        return new FilmResponse(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                                film.getDuration(), mpa, genres, film.getLikes());
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
    }


    public Collection<Genre> findAllGenres() {
        return Arrays.asList(Genre.values());
    }

    public Genre findGenreById(int id) {
        return Genre.fromId(id);
    }

    public List<MpaRatingResponse> findAllMpaRatings() {
        return Arrays.stream(MpaRating.values())
                     .map(mpa -> new MpaRatingResponse(mpa.getId(), mpa.getName()))
                     .collect(Collectors.toList());
    }

    public MpaRatingResponse findMpaRatingById(int id) {
        MpaRating mpa = MpaRating.fromId(id);
        return new MpaRatingResponse(mpa.getId(), mpa.getName());
    }
}

