package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerUT {

  @Test
  void postUser_ShouldReturnCreatedUser() {
    // Arrange
    UserService userService = Mockito.mock(UserService.class);
    UserController userController = new UserController(userService);

    User inputUser = new User(null, "John", "Doe", "john.doe@example.com", "1234567890");
    User createdUser = new User(1L, "John", "Doe", "john.doe@example.com", "1234567890");

    when(userService.createUser(inputUser)).thenReturn(createdUser);

    // Act
    ResponseEntity<User> response = userController.createUser(inputUser);

    // Assert
    assertEquals(201, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(createdUser, response.getBody());
    verify(userService, times(1)).createUser(inputUser);
  }
}
