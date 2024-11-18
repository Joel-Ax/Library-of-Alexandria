package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.LoanRepository;
import com.example.libraryofalexandria.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LoanRepository loanRepository;

  @Test
  void postUser_ShouldReturnCreatedUser() throws Exception {
    // Arrange
    String userJson = """
            {
                "firstName": "Jane",
                "lastName": "Doe",
                "email": "jane.doe@example.com",
                "memberNumber": "9876543210"
            }
        """;

    // Act & Assert
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
        .andExpect(jsonPath("$.memberNumber").value("9876543210"));

    // Clean up
    loanRepository.deleteAll(); // Clear dependent records
    userRepository.deleteAll(); // Clear users
  }
}
