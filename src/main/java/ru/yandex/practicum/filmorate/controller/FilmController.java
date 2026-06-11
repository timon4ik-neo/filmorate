package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Film created: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film with id=" + film.getId() + " was not found");
        }
        films.put(film.getId(), film);
        log.info("Film updated: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name must not be blank");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Film description must not be longer than 200 characters");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Film release date must be specified");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            throw new ValidationException("Film release date must not be earlier than December 28, 1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Film duration must be positive");
        }
    }
}
