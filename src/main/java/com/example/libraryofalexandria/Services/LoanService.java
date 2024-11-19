package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan borrowBook(Book book, User user) {
        if (book.getAvailable() == null || !book.getAvailable()) {
            throw new RuntimeException("The book is already borrowed");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30)); // Set due date
        loan.setReturned(false); // Ensure it's not returned

        // Save the loan and ensure the transaction is flushed
        loanRepository.save(loan);
        loanRepository.flush();  // Forces the loan to be saved immediately

        book.setAvailable(false); // Mark the book as not available
        return loan;
    }

    public List<Loan> getActiveLoans(User user) {
        return loanRepository.findByUserAndReturnedDateIsNull(user);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        loan.setReturned(true);
        loan.setReturnedDate(LocalDate.now());

        return loanRepository.save(loan);
    }
}


