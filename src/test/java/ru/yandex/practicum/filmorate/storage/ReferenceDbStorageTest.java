package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReferenceDbStorageTest {
    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private MpaStorage mpaStorage;

    @Test
    void shouldReturnCompleteReferenceData() {
        assertThat(genreStorage.findAll())
                .hasSize(6)
                .first()
                .isEqualTo(new Genre(1, "Комедия"));
        assertThat(mpaStorage.findAll())
                .hasSize(5)
                .last()
                .isEqualTo(new Mpa(5, "NC-17"));
    }

    @Test
    void shouldFailForUnknownReferenceIds() {
        assertThatThrownBy(() -> genreStorage.getById(9999)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> mpaStorage.getById(9999)).isInstanceOf(NotFoundException.class);
    }
}
