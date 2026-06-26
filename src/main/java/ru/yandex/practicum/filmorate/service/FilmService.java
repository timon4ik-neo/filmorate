package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Defines business operations for films.
 */
public interface FilmService {
    /**
     * Validates and saves a new film.
     *
     * @param film film data from the request body
     * @return saved film with an assigned identifier
     */
    Film addFilm(Film film);

    /**
     * Validates and updates an existing film.
     *
     * @param film film data with an existing identifier
     * @return updated film
     */
    Film updateFilm(Film film);

    /**
     * Returns all saved films.
     *
     * @return collection of films
     */
    Collection<Film> getFilms();

    /**
     * Returns a film by identifier.
     *
     * @param id film identifier
     * @return found film
     */
    Film getFilm(int id);

    /**
     * Adds a user's like to a film.
     *
     * @param filmId film identifier
     * @param userId user identifier
     */
    void addLike(int filmId, int userId);

    /**
     * Removes a user's like from a film.
     *
     * @param filmId film identifier
     * @param userId user identifier
     */
    void removeLike(int filmId, int userId);

    /**
     * Returns the most popular films sorted by like count.
     *
     * @param count maximum number of films to return
     * @return collection of popular films
     */
    Collection<Film> getPopularFilms(int count);
}
