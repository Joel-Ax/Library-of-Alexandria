package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Genre;
import com.example.libraryofalexandria.Services.GenreService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class GenreControllerUnitTest {

    @InjectMocks
    private GenreController genreController;

    @Mock
    private GenreService genreService;

    public GenreControllerUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGenre_Success() {

        Genre inputGenre = new Genre();
        inputGenre.setName("Fiction");

        Genre savedGenre = new Genre();
        savedGenre.setId(1L);
        savedGenre.setName("Fiction");

        when(genreService.createGenre(inputGenre)).thenReturn(savedGenre);

        ResponseEntity<Genre> response = genreController.createGenre(inputGenre);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(savedGenre, response.getBody());
    }

    @Test
    void testCreateGenre_BadRequest() {

        Genre inputGenre = new Genre();
        inputGenre.setName(null);

        doThrow(new RuntimeException("Invalid Genre")).when(genreService).createGenre(inputGenre);

        ResponseEntity<Genre> response = genreController.createGenre(inputGenre);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }
}
