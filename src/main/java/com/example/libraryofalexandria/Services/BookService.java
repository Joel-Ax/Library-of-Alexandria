package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Exceptions.ResourceNotFoundException;
import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Genre;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if(book.hasGenre(genre)) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        //List<Loan> loans = loanRepository.findByBookId(id);
        //loanRepository.deleteAll(loans);
        bookRepository.delete(book);
    }

    /*public Book deleteBook(Book book) {
        Book bookToDelete = bookRepository.findById(book.getId()).orElse(null);
        if (bookToDelete != null) {
            bookRepository.delete(bookToDelete);
        }
        return bookToDelete;
    }*/

    public List<Book> getBooksWithNoGenre() {
        List<Book> allBooks = bookRepository.findAll();
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if(book.getGenres().isEmpty()) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }
}
