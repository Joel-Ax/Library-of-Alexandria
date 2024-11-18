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
        loan.setDueDate(LocalDate.now());
        loan.setReturned(false);

        book.setAvailable(false);
        return loanRepository.save(loan);
    }


    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        loan.setReturned(true);
        loan.setReturnedDate(LocalDate.now());

        return loanRepository.save(loan);
    }

    public List<Loan> getActiveLoans(User user) {
        return loanRepository.findByUser(user);
    }
}
