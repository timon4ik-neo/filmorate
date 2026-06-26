package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public User add(User user) {
        user.setId(nextId++);
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.debug("User saved to storage: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        User storedUser = getById(user.getId());
        user.setFriends(new HashSet<>(storedUser.getFriends()));
        users.put(user.getId(), user);
        log.debug("User updated in storage: id={}", user.getId());
        return user;
    }

    @Override
    public void delete(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        users.remove(id);
        log.debug("User deleted from storage: id={}", id);
    }

    @Override
    public User getById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }
}
