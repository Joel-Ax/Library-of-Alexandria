package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.LoanRepository;
import com.example.libraryofalexandria.Services.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceUnitTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void borrowBook() {
        MockitoAnnotations.openMocks(this);

        // Mockar bok
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Hobbit");
        book.setAvailable(true);

        // Mockar ny user1
        User user = new User();
        user.setId(1L);
        user.setFirstName("Mike");
        user.setLastName("Tyson");

        // Mockar repo
        Loan mockLoan = new Loan();
        mockLoan.setId(1L);
        mockLoan.setBook(book);
        mockLoan.setUser(user);
        mockLoan.setLoanDate(LocalDate.now());
        mockLoan.setDueDate(LocalDate.now().plusDays(30));
        mockLoan.setReturned(false);

        when(loanRepository.save(any(Loan.class))).thenReturn(mockLoan);

        // Call the borrowBook method
        Loan result = loanService.borrowBook(book, user);

        // Verify interactions with the repository
        verify(loanRepository, times(1)).save(any(Loan.class));

        // Assertions
        assertNotNull(result);
        assertEquals("The Hobbit", result.getBook().getTitle());
        assertEquals(1L, result.getUser().getId());
        assertFalse(result.getReturned());
        assertFalse(book.getAvailable(), "Book should be marked as unavailable after borrowing");
    }
}