package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createFilmShouldReturnCreatedFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Inception",
                                  "description": "A dream inside a dream",
                                  "releaseDate": "2010-07-16",
                                  "duration": 148
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Inception")));
    }

    @Test
    void createFilmShouldRejectTooEarlyReleaseDate() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Too early",
                                  "description": "No camera yet",
                                  "releaseDate": "1895-12-27",
                                  "duration": 10
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmShouldReturnUpdatedFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Original",
                                  "description": "Original description",
                                  "releaseDate": "2001-01-01",
                                  "duration": 100
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "name": "Updated",
                                  "description": "Updated description",
                                  "releaseDate": "2001-01-01",
                                  "duration": 110
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void createUserShouldUseLoginAsNameWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "login": "userlogin",
                                  "name": "",
                                  "birthday": "1995-03-21"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("userlogin")));
    }

    @Test
    void createUserShouldRejectLoginWithSpaces() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "login": "user login",
                                  "birthday": "1995-03-21"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUnknownUserShouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 99,
                                  "email": "user@example.com",
                                  "login": "userlogin",
                                  "birthday": "1995-03-21"
                                }
                                """))
                .andExpect(status().isNotFound());
    }
}
