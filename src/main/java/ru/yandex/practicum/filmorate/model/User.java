package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Login must not be blank")
    private String login;

    private String name;

    @NotNull(message = "Birthday must be specified")
    @PastOrPresent(message = "Birthday must not be in the future")
    private LocalDate birthday;
}
