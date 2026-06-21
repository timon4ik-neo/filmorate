package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void friendsEndpointsShouldAddRemoveAndReturnCommonFriends() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "first@example.com",
                                  "login": "first",
                                  "birthday": "1995-03-21"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "second@example.com",
                                  "login": "second",
                                  "birthday": "1995-03-22"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "third@example.com",
                                  "login": "third",
                                  "birthday": "1995-03-23"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/3/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)));

        mockMvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)));

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void likesEndpointsShouldUpdateLikesAndReturnPopularFilms() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "first@example.com",
                                  "login": "first",
                                  "birthday": "1995-03-21"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "second@example.com",
                                  "login": "second",
                                  "birthday": "1995-03-22"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "First film",
                                  "description": "First description",
                                  "releaseDate": "2001-01-01",
                                  "duration": 100
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Second film",
                                  "description": "Second description",
                                  "releaseDate": "2002-01-01",
                                  "duration": 110
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/2/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes.length()", is(1)));
    }
}
