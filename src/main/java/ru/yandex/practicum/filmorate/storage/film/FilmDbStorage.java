package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_FILMS = "SELECT f.film_id, f.name, f.description, f.release_date, " +
            "f.duration, m.mpa_id, m.name AS mpa_name FROM films f " +
            "JOIN mpa_ratings m ON m.mpa_id = f.mpa_id";

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper,
                         GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    @Transactional
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        return getById(film.getId());
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        int updated = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        if (updated == 0) {
            throw filmNotFound(film.getId());
        }
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        return getById(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SELECT_FILMS + " ORDER BY f.film_id", filmRowMapper);
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Film getById(int filmId) {
        List<Film> films = jdbcTemplate.query(SELECT_FILMS + " WHERE f.film_id = ?", filmRowMapper, filmId);
        if (films.isEmpty()) {
            throw filmNotFound(filmId);
        }
        Film film = films.get(0);
        loadGenres(film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)",
                filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String sql = SELECT_FILMS + " LEFT JOIN likes l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id ASC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        films.forEach(this::loadGenres);
        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .forEach(genreId -> jdbcTemplate.update(
                        "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genreId));
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.genre_id, g.name AS genre_name FROM genres g " +
                "JOIN film_genres fg ON fg.genre_id = g.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
        film.setGenres(new LinkedHashSet<>(jdbcTemplate.query(sql, genreRowMapper, film.getId())));
    }

    private NotFoundException filmNotFound(int filmId) {
        return new NotFoundException("Film with id=" + filmId + " was not found");
    }
}
