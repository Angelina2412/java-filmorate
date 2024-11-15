package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private FilmService filmService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void testFindExistingFilm() {
        Film film = filmDbStorage.findByIdFilm(1L);

        assertThat(film)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Film Updated")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 4, 17));

        if (film.getMpaRating() != null) {
            assertThat(film.getMpaRating())
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", 5);
        }

        List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(1, 2);
        }
    }


    @Test
    @Order(4)
    void testCreateFilm() {
        Film newFilm = new Film();
        newFilm.setName("New Film");
        newFilm.setDescription("Description of new film");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(120);

        newFilm.setMpaRating(null);
        newFilm.setGenres(null);

        Film createdFilm = filmService.create(newFilm);

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("New Film");

        Film foundFilm = filmDbStorage.findByIdFilm(createdFilm.getId());
        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getName()).isEqualTo("New Film");

        assertThat(foundFilm.getGenres()).isNull();
        assertThat(foundFilm.getMpaRating()).isNull();
    }

    @Test
    @Order(6)
    void testUpdateFilm() {
        Film filmToUpdate = filmDbStorage.findByIdFilm(1L);
        filmToUpdate.setName("Updated Film Name");
        filmToUpdate.setDescription("Updated description");

        Film updatedFilm = filmService.update(filmToUpdate);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");

        Film foundFilm = filmDbStorage.findByIdFilm(1L);
        assertThat(foundFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(foundFilm.getDescription()).isEqualTo("Updated description");

        List<Genre> genres = foundFilm.getGenres();
        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(2, 3);
    }

    @Test
    @Order(5)
    void testDeleteFilm() {
        Film newFilm = new Film();
        newFilm.setName("Film to be deleted");
        newFilm.setDescription("This film will be deleted");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(100);

        newFilm.setMpaRating(null);
        newFilm.setGenres(null);

        Film createdFilm = filmService.create(newFilm);
        Long filmId = createdFilm.getId();

        filmService.removeLike(filmId, 1L);

        assertThrows(EmptyResultDataAccessException.class, () -> filmDbStorage.findByIdFilm(filmId));
    }

    @Test
    @Order(2)
    void testFindGenresByFilmId() {
        Film film = filmDbStorage.findByIdFilm(1L);
        List<Genre> genres = film.getGenres();

        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @Order(3)
    void testFindMpaRatingById() {
        MpaRating mpaRating = filmDbStorage.findMpaRatingById(1);

        assertThat(mpaRating)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

}








