package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Author;
import com.example.libraryofalexandria.Services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping
public class AuthorController {

    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
         List<Author> authors = authorService.getAllAuthors();
         return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        Author createdAuthor = authorService.createAuthor(author);
        return new ResponseEntity<>(createdAuthor, HttpStatus.CREATED);
    }
}
