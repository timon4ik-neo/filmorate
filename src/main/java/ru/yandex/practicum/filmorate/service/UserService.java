package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        prepareUser(user);
        User savedUser = userStorage.add(user);
        log.info("User created: id={}, login={}", savedUser.getId(), savedUser.getLogin());
        return savedUser;
    }

    public User updateUser(User user) {
        validateUser(user);
        prepareUser(user);
        User updatedUser = userStorage.update(user);
        log.info("User updated: id={}, login={}", updatedUser.getId(), updatedUser.getLogin());
        return updatedUser;
    }

    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(int id) {
        return userStorage.getById(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Users became friends: userId={}, friendId={}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Users are no longer friends: userId={}, friendId={}", userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        User user = userStorage.getById(userId);
        return user.getFriends()
                .stream()
                .map(userStorage::getById)
                .toList();
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherId);
        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());
        return commonFriendIds
                .stream()
                .map(userStorage::getById)
                .toList();
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email must be valid");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Login must not be blank");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login must not contain spaces");
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Birthday must be specified");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday must not be in the future");
        }
    }

    private void prepareUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
