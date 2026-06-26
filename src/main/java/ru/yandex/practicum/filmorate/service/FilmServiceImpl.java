package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        Film savedFilm = filmStorage.add(film);
        log.info("Film created: id={}, name={}", savedFilm.getId(), savedFilm.getName());
        return savedFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        Film updatedFilm = filmStorage.update(film);
        log.info("Film updated: id={}, name={}", updatedFilm.getId(), updatedFilm.getName());
        return updatedFilm;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    @Override
    public Film getFilm(int id) {
        return filmStorage.getById(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        film.getLikes().add(userId);
        log.info("User id={} liked film id={}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        film.getLikes().remove(userId);
        log.info("User id={} removed like from film id={}", userId, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        if (count < 0) {
            throw new ValidationException("Popular films count must not be negative");
        }
        return filmStorage.getAll()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
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
