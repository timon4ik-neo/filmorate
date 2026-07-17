package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FilmDbStorageTest {
    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Autowired
    private FilmService filmService;

    @Test
    void shouldStoreAndReadFilmWithRatingAndOrderedUniqueGenres() {
        Film film = newFilm("Film");
        film.setGenres(new LinkedHashSet<>(List.of(new Genre(2, null), new Genre(1, null))));

        Film created = filmService.create(film);

        assertThat(created.getId()).isPositive();
        assertThat(created.getMpa()).isEqualTo(new Mpa(3, "PG-13"));
        assertThat(created.getGenres())
                .extracting(Genre::getId)
                .containsExactly(1, 2);
    }

    @Test
    void shouldOrderPopularFilmsByLikeCount() {
        Film first = filmService.create(newFilm("First"));
        Film second = filmService.create(newFilm("Second"));
        User user = userStorage.create(User.builder()
                .email("viewer@example.com")
                .login("viewer")
                .name("Viewer")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        filmStorage.addLike(second.getId(), user.getId());

        assertThat(filmStorage.getPopular(2))
                .extracting(Film::getId)
                .containsExactly(second.getId(), first.getId());
    }

    private Film newFilm(String name) {
        return Film.builder()
                .name(name)
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Mpa(3, null))
                .build();
    }
}
