package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Endpoint to get all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Book> getBookByTitle(@PathVariable String title) {
        Book book = bookService.getBookByTitle(title);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBookByGenre(@PathVariable String genre) {
        List<Book> books = bookService.getBooksByGenre(genre);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // Säkerställ att inget ID skickas med
        if (book.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            // Eller mer informativt:
            // throw new IllegalArgumentException("New book should not include ID");
        }
        try {
            Book savedBook = bookService.createBook(book);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {
        Book updateBook = bookService.updateBook(book);
        return new ResponseEntity<>(updateBook, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Book> deleteBook(@RequestBody Book book) {
        Book deletedBook = bookService.deleteBook(book);
        return new ResponseEntity<>(deletedBook, HttpStatus.OK);
    }

    @GetMapping("/noGenres")
    public ResponseEntity<List<Book>> getBooksWithNoGenres() {
        List<Book> books = bookService.getBooksWithNoGenre();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
