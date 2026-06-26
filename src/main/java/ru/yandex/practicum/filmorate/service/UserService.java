package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Defines business operations for users and friendship management.
 */
public interface UserService {
    /**
     * Validates and saves a new user.
     *
     * @param user user data from the request body
     * @return saved user with an assigned identifier
     */
    User addUser(User user);

    /**
     * Validates and updates an existing user.
     *
     * @param user user data with an existing identifier
     * @return updated user
     */
    User updateUser(User user);

    /**
     * Returns all saved users.
     *
     * @return collection of users
     */
    Collection<User> getUsers();

    /**
     * Returns a user by identifier.
     *
     * @param id user identifier
     * @return found user
     */
    User getUser(int id);

    /**
     * Adds users to each other's friend lists.
     *
     * @param userId user identifier
     * @param friendId friend identifier
     */
    void addFriend(int userId, int friendId);

    /**
     * Removes users from each other's friend lists.
     *
     * @param userId user identifier
     * @param friendId friend identifier
     */
    void removeFriend(int userId, int friendId);

    /**
     * Returns a user's friends.
     *
     * @param userId user identifier
     * @return collection of friends
     */
    Collection<User> getFriends(int userId);

    /**
     * Returns friends shared by two users.
     *
     * @param userId user identifier
     * @param otherId another user identifier
     * @return collection of common friends
     */
    Collection<User> getCommonFriends(int userId, int otherId);
}
