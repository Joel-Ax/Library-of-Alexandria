package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Genre;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateGenre() throws Exception {
        Genre genre = new Genre();
        genre.setName("Science Fiction");

        String genreJson = objectMapper.writeValueAsString(genre);

        mockMvc.perform(post("/api/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(genreJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Science Fiction"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateGenreBadRequest() throws Exception {
        Genre genre = new Genre();
        genre.setName(null); // Invalid name

        String genreJson = objectMapper.writeValueAsString(genre);

        mockMvc.perform(post("/api/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(genreJson))
                .andExpect(status().isBadRequest());
    }
}
