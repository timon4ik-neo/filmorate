package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User getById(int userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> getFriends(int userId);

    Collection<User> getCommonFriends(int userId, int otherId);

    default User createUser(User user) {
        return create(user);
    }

    default User updateUser(User user) {
        return update(user);
    }

    default Collection<User> getAllUsers() {
        return findAll();
    }

    default User getUserById(int userId) {
        return getById(userId);
    }
}
