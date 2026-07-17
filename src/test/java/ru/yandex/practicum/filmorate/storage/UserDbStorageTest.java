package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserDbStorageTest {
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Test
    void shouldCreateUpdateAndFindUser() {
        User user = userStorage.create(newUser("one"));

        assertThat(user.getId()).isPositive();
        assertThat(userStorage.getById(user.getId())).isEqualTo(user);

        user.setName("Updated name");
        user.setEmail("updated@example.com");
        User updated = userStorage.update(user);

        assertThat(updated.getName()).isEqualTo("Updated name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void friendshipShouldBeOneSidedAndCommonFriendsShouldBeFound() {
        User first = userStorage.create(newUser("first"));
        User second = userStorage.create(newUser("second"));
        User common = userStorage.create(newUser("common"));

        userStorage.addFriend(first.getId(), second.getId());
        userStorage.addFriend(first.getId(), common.getId());
        userStorage.addFriend(second.getId(), common.getId());

        assertThat(userStorage.getFriends(first.getId()))
                .extracting(User::getId)
                .containsExactly(second.getId(), common.getId());
        assertThat(userStorage.getFriends(second.getId()))
                .extracting(User::getId)
                .containsExactly(common.getId());
        assertThat(userStorage.getCommonFriends(first.getId(), second.getId()))
                .extracting(User::getId)
                .containsExactly(common.getId());
    }

    private User newUser(String login) {
        return User.builder()
                .email(login + "@example.com")
                .login(login)
                .name(login)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }
}
