package ru.yandex.practicum.filmorate.controller;

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
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Валидация name не пройдена");
            throw new ValidationException("Название не может быть пустым");
        }

        String description = film.getDescription();
        if (description != null && description.length() > 200) {
            log.error("Валидация description не пройдена");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        LocalDate minReleaseDate = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("Валидация releaseDate не пройдена");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.error("Валидация duration не пройдена");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }

        film.setId(getNextId());
        log.info("Добавлен новый фильм");
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Валидация id не пройдена");
            throw new ValidationException("Id должен быть указан");
        }

        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            log.error("Верификация login не пройдена");
            throw new ValidationException.NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            log.error("Валидация name не пройдена");
            existingFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
            log.error("Валидация description не пройдена");
            existingFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Валидация releaseDate не пройдена");
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() > 0) {
            log.error("Валидация duration не пройдена");
            existingFilm.setDuration(newFilm.getDuration());
        }

        log.info("Данные о фильме обновлены");
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
