package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.message").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionTooLong() throws Exception {
        String filmJson = "{\"name\": \"Матрица\", \"description\": \"" + "A".repeat(201) + "\", \"duration\": 120, \"releaseDate\": \"2020-01-01\"}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Максимальная длина описания — 200 символов"));
    }

    @Test
    void shouldCreateFilmWhenValidDataProvided() throws Exception {
        String filmJson = "{\"name\": \"Матрица\", \"description\": \"Описание фильма\", \"duration\": 120, \"releaseDate\": \"2020-01-01\"}";

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
}

