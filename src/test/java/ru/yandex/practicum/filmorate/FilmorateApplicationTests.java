package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        System.out.println(film);

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
            assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(2);
        }
    }


    @Test
    @Order(4)
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
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(2);
    }

    @Test
    @Order(2)
    void testFindGenresByFilmId() {
        Film film = filmDbStorage.findByIdFilm(1L);
        List<Genre> genres = film.getGenres();

        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(2);
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








