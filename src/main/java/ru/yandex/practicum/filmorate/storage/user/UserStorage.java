package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Defines storage operations for users.
 */
public interface UserStorage {
    /**
     * Saves a new user.
     *
     * @param user user data to save
     * @return saved user with an assigned identifier
     */
    User add(User user);

    /**
     * Updates an existing user.
     *
     * @param user user data with an existing identifier
     * @return updated user
     */
    User update(User user);

    /**
     * Deletes a user by identifier.
     *
     * @param id user identifier
     */
    void delete(int id);

    /**
     * Finds a user by identifier.
     *
     * @param id user identifier
     * @return found user
     */
    User getById(int id);

    /**
     * Returns all saved users.
     *
     * @return collection of users
     */
    Collection<User> getAll();
}
