package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.model.MpaRating;

public class MpaRatingWrapper {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private final Integer id;

    public MpaRatingWrapper(MpaRating mpaRating) {
        this.id = (mpaRating != null) ? mpaRating.getId() : null;
    }

    public Integer getId() {
        return id;
    }

    public MpaRating getMpaRating() {
        return id != null ? MpaRating.fromId(id) : null;
    }
}

