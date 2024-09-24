package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnBadRequestWhenFilmNameIsEmpty() throws Exception {
        String filmJson = "{\"name\": \"\", \"description\": \"Описание фильма\", \"duration\": 120, \"releaseDate\": \"2020-01-01\"}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionTooLong() throws Exception {
        String filmJson = "{\"name\": \"Матрица\", \"description\": \"" + "A".repeat(201) +
                "\", \"duration\": 120, \"releaseDate\": \"2020-01-01\"}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    void shouldCreateFilmWhenValidDataProvided() throws Exception {
        String filmJson =
                "{\"name\": \"Матрица\", \"description\": \"Описание фильма\", \"duration\": 120, \"releaseDate\": \"2020-01-01\"}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Матрица"));
    }

    @Test
    void shouldReturnBadRequestWhenUserLoginIsEmpty() throws Exception {
        String userJson = "{\"email\": \"@cat123\", \"login\": \"\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Логин не может быть пустым и не должен содержать пробелы"));
    }

    @Test
    void shouldReturnBadRequestWhenUserEmailIsEmpty() throws Exception {
        String userJson = "{\"email\": \"\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Email не может быть пустым и должен содержать символ @"));
    }

    @Test
    void shouldReturnBadRequestWhenUserEmailNotContainsSymbol() throws Exception {
        String userJson = "{\"email\": \"cat123\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Email не может быть пустым и должен содержать символ @"));
    }

    @Test
    void shouldReturnOkWhenUserNotContainsName() throws Exception {
        String userJson = "{\"email\": \"@cat123\", \"login\": \"Kitty\", \"name\": \"\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Kitty"));
    }

    @Test
    void shouldReturnBadRequestWhenReleaseDateIsTooEarly() throws Exception {
        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1890-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldReturnOkWhenUserAddsLikeToFilm() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1"))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUserAddsLikeToNotExistFilm() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/films/199/like/1"))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUserWithWrongIdAddsLikeToFilm() throws Exception {
        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/199"))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUserDeletesLikeToFilm() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(delete("/films/1/like/1"))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUserDeletesLikeToNotExistFilm() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(delete("/films/100/like/1"))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUserWithWrongIdDeletesLikeToFilm() throws Exception {
        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(delete("/films/1/like/134"))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUserAddsFriend() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        String userFriendsJson = "{\"email\": \"user2@gmail.com\", \"login\": \"Kitty2\", \"name\": \"Leooo\", \"birthday\": " +
                "\"2001-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userFriendsJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2"))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUserAddsNotExistFriend() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2"))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUserDeletesFriend() throws Exception {
        String userJson = "{\"email\": \"user@gmail.com\", \"login\": \"Kitty\", \"name\": \"Leo\", \"birthday\": \"2000-01-01\"}";

        String userFriendsJson =
                "{\"email\": \"user2@gmail.com\", \"login\": \"Kitty2\", \"name\": \"Leooo\", \"birthday\": \"2001-01-01\"}";

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userFriendsJson))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2"))
               .andDo(print())
               .andExpect(status().isOk());

        mockMvc.perform(delete("/users/1/friends/2"))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSystemErrorWhenFilmNotExist() throws Exception {
        String filmJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 200}";

        mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
               .andDo(print())
               .andExpect(status().isOk());
        String updateJson = "{\"name\": \"Name\", \"description\": \"Description\", \"releaseDate\": \"1990-03-25\", \"duration\": 300}";

        mockMvc.perform(put("/films/45").contentType(MediaType.APPLICATION_JSON)
                                       .content(updateJson))
               .andDo(print())
               .andExpect(status().isNotFound());
    }
}




