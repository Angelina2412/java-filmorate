package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Отображается список всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("Валидация releaseDate не пройдена");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм с id = {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Фильм не найден по id");
            throw new IllegalArgumentException("Id должен быть указан");
        }

        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            log.error("Фильм с id = {} не найден", newFilm.getId());
            throw new IllegalArgumentException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        existingFilm.setName(newFilm.getName());
        existingFilm.setDescription(newFilm.getDescription());
        existingFilm.setReleaseDate(newFilm.getReleaseDate());
        existingFilm.setDuration(newFilm.getDuration());

        log.info("Данные о фильме с id = {} обновлены", newFilm.getId());
        return existingFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
