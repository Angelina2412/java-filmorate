package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaRatingController {

    private final FilmService filmService;

    public MpaRatingController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<MpaRating> findAllMpa() {
        log.info("Отображается список всех рейтингов");
        return filmService.findAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating findMpaById(@PathVariable int id) {
        log.info("Получение рейтинга с id = {}", id);
        MpaRating mpaRating = filmService.findMpaRatingById(id);
        if (mpaRating == null) {
            throw new NotFoundException("Рейтинг с таким id отсутствует: " + id);
        }
        return mpaRating;
    }
}

