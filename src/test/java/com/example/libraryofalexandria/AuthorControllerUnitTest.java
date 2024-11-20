package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Controllers.AuthorController;
import com.example.libraryofalexandria.Models.Author;
import com.example.libraryofalexandria.Services.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorControllerUnitTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testCreateAuthor() {
        // Arrange (mockar en ny user)
        Author mockAuthor = new Author();
        mockAuthor.setId(1L);
        mockAuthor.setFirstName("Jakob");
        mockAuthor.setLastName("Olsson");
        mockAuthor.setBirthDate(LocalDate.of(2000, 8, 3));

        when(authorService.createAuthor(mockAuthor)).thenReturn(mockAuthor);

        // Act
        ResponseEntity<Author> response = authorController.createAuthor(mockAuthor);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockAuthor, response.getBody());
        assertEquals("Jakob", response.getBody().getFirstName());
        assertEquals("Olsson", response.getBody().getLastName());
        assertEquals(LocalDate.of(2000, 8, 3), response.getBody().getBirthDate());
    }
}
