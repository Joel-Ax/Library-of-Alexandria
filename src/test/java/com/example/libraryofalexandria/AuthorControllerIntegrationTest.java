package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Author;
import com.example.libraryofalexandria.Models.Genre;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional // Gör testdatan ogjord
public class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAuthorAsAdmin() throws Exception {
        // Arrange
        String jsonAuthor = "{\"firstName\": \"Jakob\", \"lastName\": \"Olsson\", \"birthDate\": \"2000-08-03\" }";

        // Act
        mockMvc.perform(post("/")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthor))
                .andExpect(status().isCreated()) // Assert
                .andExpect(jsonPath("$.firstName").value("Jakob"))
                .andExpect(jsonPath("$.lastName").value("Olsson"))
                .andExpect(jsonPath("$.birthDate").value("2000-08-03"));
    }

    @Test
    void createAuthorWithoutAuthentication() throws Exception {
        // Arrange
        String jsonAuthor = "{\"firstName\": \"Jakob\", \"lastName\": \"Olsson\", \"birthDate\": \"2000-08-03\" }";

        // Act
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthor))
                .andExpect(status().isUnauthorized()); // Assert
    }

}
