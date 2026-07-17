package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private static final String SELECT_GENRES = "SELECT genre_id, name AS genre_name FROM genres";

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper rowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query(SELECT_GENRES + " ORDER BY genre_id", rowMapper);
    }

    @Override
    public Genre getById(int genreId) {
        List<Genre> genres = jdbcTemplate.query(SELECT_GENRES + " WHERE genre_id = ?", rowMapper, genreId);
        if (genres.isEmpty()) {
            throw new NotFoundException("Genre with id=" + genreId + " was not found");
        }
        return genres.get(0);
    }
}
