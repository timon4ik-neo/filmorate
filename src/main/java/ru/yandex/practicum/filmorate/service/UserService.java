package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validateAndPrepare(user);
        User created = userStorage.create(user);
        log.info("User created: id={}, login={}", created.getId(), created.getLogin());
        return created;
    }

    public User update(User user) {
        userStorage.getById(user.getId());
        validateAndPrepare(user);
        User updated = userStorage.update(user);
        log.info("User updated: id={}, login={}", updated.getId(), updated.getLogin());
        return updated;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(int userId) {
        return userStorage.getById(userId);
    }

    public void addFriend(int userId, int friendId) {
        validateFriendPair(userId, friendId);
        userStorage.addFriend(userId, friendId);
        log.info("User {} added user {} as a friend", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        validateFriendPair(userId, friendId);
        userStorage.removeFriend(userId, friendId);
        log.info("User {} removed user {} from friends", userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        userStorage.getById(userId);
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        userStorage.getById(userId);
        userStorage.getById(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    private void validateAndPrepare(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not be blank or contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateFriendPair(int userId, int friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
        if (userId == friendId) {
            throw new ValidationException("A user cannot add themselves as a friend");
        }
    }
}
