package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private static final String SELECT_USERS = "SELECT user_id, email, login, name, birthday FROM users";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return getById(user.getId());
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()), user.getId());
        if (updated == 0) {
            throw userNotFound(user.getId());
        }
        return getById(user.getId());
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(SELECT_USERS + " ORDER BY user_id", rowMapper);
    }

    @Override
    public User getById(int userId) {
        List<User> users = jdbcTemplate.query(SELECT_USERS + " WHERE user_id = ?", rowMapper, userId);
        if (users.isEmpty()) {
            throw userNotFound(userId);
        }
        return users.get(0);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update("MERGE INTO friendships (user_id, friend_id, confirmed) " +
                "KEY (user_id, friend_id) VALUES (?, ?, FALSE)", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        String sql = SELECT_USERS + " WHERE user_id IN " +
                "(SELECT friend_id FROM friendships WHERE user_id = ?) ORDER BY user_id";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {
        String sql = SELECT_USERS + " WHERE user_id IN (" +
                "SELECT f1.friend_id FROM friendships f1 " +
                "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?) ORDER BY user_id";
        return jdbcTemplate.query(sql, rowMapper, userId, otherId);
    }

    private NotFoundException userNotFound(int userId) {
        return new NotFoundException("User with id=" + userId + " was not found");
    }
}
