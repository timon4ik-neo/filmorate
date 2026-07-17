package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film create(Film film) {
        validateAndEnrich(film);
        Film created = filmStorage.create(film);
        log.info("Film created: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    public Film update(Film film) {
        filmStorage.getById(film.getId());
        validateAndEnrich(film);
        Film updated = filmStorage.update(film);
        log.info("Film updated: id={}, name={}", updated.getId(), updated.getName());
        return updated;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(int filmId) {
        return filmStorage.getById(filmId);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        filmStorage.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    public Collection<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Popular film count must be positive");
        }
        return filmStorage.getPopular(count);
    }

    private void validateAndEnrich(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            throw new ValidationException("Film release date must not be earlier than December 28, 1895");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("MPA rating must be specified");
        }
        film.setMpa(mpaStorage.getById(film.getMpa().getId()));

        Map<Integer, Genre> genresById = new TreeMap<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                Genre storedGenre = genreStorage.getById(genre.getId());
                genresById.put(storedGenre.getId(), storedGenre);
            }
        }
        film.setGenres(new LinkedHashSet<>(genresById.values()));
    }
}
