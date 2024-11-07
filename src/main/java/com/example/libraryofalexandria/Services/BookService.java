package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {this.bookRepository = bookRepository;}

    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    public Book getBookByTitle(String title) {return bookRepository.findByTitle(title);}

    public Book getBookByAuthor(String author) {return bookRepository.findByAuthor(author);}

    public Book createBook(Book book) {
        return bookRepository.save(book);}

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public Book deleteBook(Book book) {
        // Assuming Book has an ID field, you can look it up by ID.
        Book bookToDelete = bookRepository.findById(book.getId()).orElse(null);
        if (bookToDelete != null) {
            bookRepository.delete(bookToDelete);
        }
        return bookToDelete;
    }

}
