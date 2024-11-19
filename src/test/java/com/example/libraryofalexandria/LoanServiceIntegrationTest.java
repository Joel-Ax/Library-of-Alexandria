package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Author;
import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.BookRepository;
import com.example.libraryofalexandria.Repositories.LoanRepository;
import com.example.libraryofalexandria.Repositories.UserRepository;
import com.example.libraryofalexandria.Services.LoanService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanServiceIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Book book;

    @Transactional
    @BeforeEach
    public void setUp() {
        loanRepository.deleteAll();

        // Hämta user från databasen
        user = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found in the database"));
        if (user == null) {
            throw new RuntimeException("User not found in the database");
        }

        // Hämta bok
        book = bookRepository.findByTitle("Pippi Långstrump");
        if (book == null) {
            throw new RuntimeException("Book not found in the database");
        }

        if (book.getAvailable() == null || !book.getAvailable()) {
            throw new RuntimeException("Book is not available for borrowing");
        }

        // Skapar nytt lån
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturned(false);

        loanRepository.save(loan);
    }

    @Test
    public void testBorrowBook() {

        List<Loan> loans = loanRepository.findAll();
        assertFalse(loans.isEmpty(), "No loans found in the database");

        Loan loan = loans.get(0);

        assertNotNull(loan.getUser(), "Loan user is null");
        assertNotNull(loan.getBook(), "Loan book is null");
        assertEquals("Pippi Långstrump", loan.getBook().getTitle(), "Book title does not match");
        assertEquals("erik.eriksson@email.com", loan.getUser().getEmail(), "User email does not match");
        assertFalse(loan.getReturned(), "Loan should not be marked as returned");
    }

    @Test
    public void testUnavailableBook() {
        // Hämtar användare
        user = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found in the database"));

        // Mockar författare
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Stephen");
        author.setLastName("King");
        author.setBirthDate(LocalDate.of(1947, 9, 21));

        // Mockar bok
        Book unavailableBook = new Book();
        unavailableBook.setTitle("IT");
        unavailableBook.setPublication_year(1986);
        unavailableBook.setAuthor(author);
        unavailableBook.setAvailable(false);

        bookRepository.save(unavailableBook);

        assertThrows(RuntimeException.class, () -> {
            loanService.borrowBook(unavailableBook, user);
        }, "Expected an exception because the book is not available for borrowing");
    }
}
