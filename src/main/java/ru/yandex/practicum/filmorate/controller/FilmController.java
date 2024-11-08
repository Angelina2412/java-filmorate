package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Отображается список всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Film film) {
        log.info("Обновление данных о фильме: {}", film);
        try {
            Film updatedFilm = filmService.update(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма: ", e);
            return ResponseEntity.badRequest().body("Ошибка обновления фильма. Проверьте данные.");
        }
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

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Long id, @Valid @RequestBody Film film) {
        log.info("Обновляется информация у фильма {}", film);
        Film updatedFilm = filmService.updateFilm(id, film);
        return updatedFilm;
    }

    @GetMapping("/{id}")
    public FilmResponse findFilmById(@PathVariable Long id) {
        log.info("Получение фильма с id = {}", id);
        return filmService.findFilmById(id);
    }

}