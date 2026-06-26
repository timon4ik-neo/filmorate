package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Defines storage operations for films.
 */
public interface FilmStorage {
    /**
     * Saves a new film.
     *
     * @param film film data to save
     * @return saved film with an assigned identifier
     */
    Film add(Film film);

    /**
     * Updates an existing film.
     *
     * @param film film data with an existing identifier
     * @return updated film
     */
    Film update(Film film);

    /**
     * Deletes a film by identifier.
     *
     * @param id film identifier
     */
    void delete(int id);

    /**
     * Finds a film by identifier.
     *
     * @param id film identifier
     * @return found film
     */
    Film getById(int id);

    /**
     * Returns all saved films.
     *
     * @return collection of films
     */
    Collection<Film> getAll();
}
