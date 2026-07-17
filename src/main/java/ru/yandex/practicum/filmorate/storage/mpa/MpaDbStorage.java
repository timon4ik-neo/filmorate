package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.Collection;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private static final String SELECT_MPA = "SELECT mpa_id, name AS mpa_name FROM mpa_ratings";

    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper rowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query(SELECT_MPA + " ORDER BY mpa_id", rowMapper);
    }

    @Override
    public Mpa getById(int mpaId) {
        List<Mpa> ratings = jdbcTemplate.query(SELECT_MPA + " WHERE mpa_id = ?", rowMapper, mpaId);
        if (ratings.isEmpty()) {
            throw new NotFoundException("MPA rating with id=" + mpaId + " was not found");
        }
        return ratings.get(0);
    }
}
