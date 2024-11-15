package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Genre> findAllGenres() {
        log.info("Отображается список всех жанров");
        return filmService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable int id) {
        log.info("Получение жанра с id = {}", id);
        Genre genre = filmService.findGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с таким id отсутствует: " + id);
        }
        return genre;
    }
}
