package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;

    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Отображается список всех фильмов");
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film);
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление данных о фильме: {}", film);
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id = {} ставит лайк фильму с id = {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id = {} удаляет лайк у фильма с id = {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Отображается топ-{} самых популярных фильмов", count);
        return filmService.getMostPopularFilms(count);
    }

    @PutMapping("/films/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable Long id, @RequestBody Film film) {
        if (!filmService.exists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Film updatedFilm = filmStorage.update(film);
        return ResponseEntity.ok(updatedFilm);
    }

}
