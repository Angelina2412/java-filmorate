package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

        MpaRating mpaRating = findMpaRatingById(rs.getInt("rating_id"));
        film.setMpaRating(mpaRating);

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
            ps.setInt(5, film.getMpaRating().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            throw new RuntimeException("Идентификатор фильма (ID) должен быть указан для обновления");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                                               film.getName(),
                                               film.getDescription(),
                                               java.sql.Date.valueOf(film.getReleaseDate()),
                                               film.getDuration(),
                                               film.getMpaRating().getId(),
                                               film.getId());

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

    private List<Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        String sql = "INSERT INTO film_genres (film_id, genre_id) " +
                "SELECT ?, ? FROM dual WHERE NOT EXISTS (SELECT 1 FROM film_genres WHERE film_id = ? AND genre_id = ?)";

        for (Genre genre : film.getGenres()) {
            if (genre != null) {
                jdbcTemplate.update(sql, film.getId(), genre.getId(), film.getId(), genre.getId());
            }
        }
    }


    private Set<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }

    @Override
    public Genre findGenreById(int id) {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));
                return genre;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    @Override
    public List<MpaRating> findAllMpaRatings() {
        String sql = "SELECT rating_id, name FROM mpa_ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MpaRating rating = new MpaRating();
            rating.setId(rs.getInt("rating_id"));
            rating.setName(rs.getString("name"));
            return rating;
        });
    }

    @Override
    public MpaRating findMpaRatingById(int id) {
        try {
            String sql = "SELECT rating_id, name FROM mpa_ratings WHERE rating_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                MpaRating rating = new MpaRating();
                rating.setId(rs.getInt("rating_id"));
                rating.setName(rs.getString("name"));
                return rating;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    public void addLike(Long filmId, Long userId) {
        String sqlCheck = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlCheck, Integer.class, filmId, userId);
        if (count == null || count == 0) {
            String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

}
