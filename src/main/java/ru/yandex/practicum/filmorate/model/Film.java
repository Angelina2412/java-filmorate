package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @JsonProperty("name")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @JsonProperty("description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("releaseDate")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    @JsonProperty("duration")
    private int duration;

    @JsonProperty("genres")
    private List<Genre> genres;

    @JsonProperty("mpa")
    private MpaRating mpaRating;

    @Builder.Default
    @JsonProperty("likes")
    private final Set<Long> likes = new HashSet<>();

    public Set<Long> getLikes() {
        return likes;
    }

    public MpaRating getMpaRating() {
        return mpaRating;
    }

    public void setMpaRating(MpaRating mpaRating) {
        this.mpaRating = mpaRating;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

}


