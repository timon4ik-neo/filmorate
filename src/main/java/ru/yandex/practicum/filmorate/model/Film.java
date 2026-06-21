package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Film name must not be blank")
    private String name;

    @Size(max = 200, message = "Film description must not be longer than 200 characters")
    private String description;

    @NotNull(message = "Film release date must be specified")
    private LocalDate releaseDate;

    @Positive(message = "Film duration must be positive")
    private int duration;

    private Set<Integer> likes = new HashSet<>();
}
