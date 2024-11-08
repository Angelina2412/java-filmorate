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
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void testFindExistingFilm() {
        Film film = filmDbStorage.findByIdFilm(1L);

        assertThat(film)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Inception")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2010, 7, 16));

        assertThat(film.getMpaRating())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

//    @Test
//    @Order(4)
//    void testCreateFilm() {
//        Film newFilm = new Film();
//        newFilm.setName("New Film");
//        newFilm.setDescription("Description of new film");
//        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
//        newFilm.setDuration(120);
//        newFilm.setMpaRating(MpaRating.fromId(2)); // PG rating
//     //   newFilm.setGenres(new HashSet<>(Arrays.asList(Genre.fromId(1), Genre.fromId(3))));
//        newFilm.setGenres(new HashSet<>(Arrays.asList(Genre.fromId(1), Genre.fromId(3))));
//
//        Film createdFilm = filmDbStorage.create(newFilm);
//
//        assertThat(createdFilm.getId()).isNotNull();
//        assertThat(createdFilm.getName()).isEqualTo("New Film");
//
//        Film foundFilm = filmDbStorage.findByIdFilm(createdFilm.getId());
//        assertThat(foundFilm).isNotNull();
//        assertThat(foundFilm.getName()).isEqualTo("New Film");
//    }

    @Test
    @Order(6)
    void testUpdateFilm() {
        Film filmToUpdate = filmDbStorage.findByIdFilm(1L);
        filmToUpdate.setName("Updated Film Name");
        filmToUpdate.setDescription("Updated description");

        Film updatedFilm = filmDbStorage.update(filmToUpdate);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");

        // Проверка, что изменения сохранены в базе данных
        Film foundFilm = filmDbStorage.findByIdFilm(1L);
        assertThat(foundFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(foundFilm.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @Order(5)
    void testDeleteFilm() {
        Film newFilm = new Film();
        newFilm.setName("Film to be deleted");
        newFilm.setDescription("This film will be deleted");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(100);
        newFilm.setMpaRating(MpaRating.fromId(1)); // G rating

        Film createdFilm = filmDbStorage.create(newFilm);
        Long filmId = createdFilm.getId();

        filmDbStorage.deleteFilm(filmId);

        assertThrows(EmptyResultDataAccessException.class, () -> filmDbStorage.findByIdFilm(filmId));
    }

//    @Test
//    @Order(2)
//    void testFindGenresByFilmId() {
//        Film film = filmDbStorage.findByIdFilm(1L);
//        Set<Genre> genres = film.getGenres();
//
//        assertThat(genres).isNotEmpty();
//        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(1, 2); // Science Fiction, Thriller
//    }

    @Test
    @Order(3)
    void testFindMpaRatingById() {
        MpaRating mpaRating = filmDbStorage.findMpaRatingById(1L);

        assertThat(mpaRating)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

}





