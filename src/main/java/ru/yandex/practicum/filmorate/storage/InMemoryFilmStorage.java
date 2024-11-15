package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Integer, Genre> genres = new HashMap<>();
    private final Map<Integer, MpaRating> mpaRatings = new HashMap<>();
    private long currentMaxId = 0;


    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findByIdFilm(Long id) {
        return films.get(id);
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public List<Genre> findAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre findGenreById(int id) {
        return genres.get(id);
    }

    @Override
    public List<MpaRating> findAllMpaRatings() {
        return new ArrayList<>(mpaRatings.values());
    }

    @Override
    public MpaRating findMpaRatingById(int id) {
        return mpaRatings.get(id);
    }

    private long getNextId() {
        return ++currentMaxId;
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return films.values().stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size())) // Сортировка по количеству лайков, от большего к меньшему
                    .limit(count)
                    .collect(Collectors.toList());
    }
    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        film.getLikes().add(userId);
    }

}


