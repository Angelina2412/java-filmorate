package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@Qualifier("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Long mpaId = rs.getLong("rating_id");
        film.setMpaRating(findMpaRatingById(mpaId));

        film.setGenres(findGenresByFilmId(film.getId()));
        film.getLikes().addAll(findLikesByFilmId(film.getId()));

        return film;
    };

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());

            if (film.getMpaRating() != null) {
                ps.setInt(5, film.getMpaRating().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            throw new RuntimeException("Идентификатор фильма (ID) должен быть указан для обновления");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                                               film.getMpaRating().getId(), film.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с таким ID не найден для обновления");
        }

        saveGenres(film);
        return film;
    }

    @Override
    public Film findByIdFilm(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, filmRowMapper, id);
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private @NotEmpty(message = "У фильма должен быть хотя бы один жанр") Set<Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> Genre.fromId(rs.getInt("genre_id")), filmId));
    }

    private void saveGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    public @NotNull(message = "У фильма должен быть указан рейтинг MPA") MpaRating findMpaRatingById(Long mpaId) {
        String sql = "SELECT * FROM mpa_ratings WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> MpaRating.fromId(rs.getInt("rating_id")), mpaId);
    }

    private Set<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }
}
