package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public Film add(Film film) {
        film.setId(nextId++);
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.debug("Film saved to storage: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        Film storedFilm = getById(film.getId());
        film.setLikes(new HashSet<>(storedFilm.getLikes()));
        films.put(film.getId(), film);
        log.debug("Film updated in storage: id={}", film.getId());
        return film;
    }

    @Override
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id=" + id + " was not found");
        }
        films.remove(id);
        log.debug("Film deleted from storage: id={}", id);
    }

    @Override
    public Film getById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Film with id=" + id + " was not found");
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }
}
