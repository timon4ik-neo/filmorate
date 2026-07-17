package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film getById(int filmId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Film> getPopular(int count);

    default Film createFilm(Film film) {
        return create(film);
    }

    default Film updateFilm(Film film) {
        return update(film);
    }

    default Collection<Film> getAllFilms() {
        return findAll();
    }

    default Film getFilmById(int filmId) {
        return getById(filmId);
    }

    default Collection<Film> getPopularFilms(int count) {
        return getPopular(count);
    }
}
