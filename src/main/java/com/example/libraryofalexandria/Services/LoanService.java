package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        loan.setDueDate(LocalDate.now().plusDays(30));
        loan.setReturned(false);

        loanRepository.save(loan);

        book.setAvailable(false);
        return loan;
    }

    public List<Loan> getActiveLoans(User user) {
        return loanRepository.findByUserAndReturnedDateIsNull(user);
    }

    public Loan returnBook(Book book, User user) {
        Loan activeLoan = getActiveLoanByBookAndUser(book, user);
        activeLoan.setReturned(true);
        activeLoan.setReturnedDate(LocalDate.now());
        loanRepository.save(activeLoan);

        book.setAvailable(true);
        return activeLoan;
    }

    public Loan getActiveLoanByBookAndUser(Book book, User user) {
        return getActiveLoans(user).stream()
                .filter(loan -> loan.getBook().equals(book))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active loan found for this book and user"));
    }

    public List<Book> getBorrowedBooks(User user) {
        return getActiveLoans(user).stream()
                .map(Loan::getBook)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getBorrowedBooksSummary(User user) {
        return getActiveLoans(user).stream()
                .map(loan -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("title", loan.getBook().getTitle());
                    summary.put("dueDate", loan.getDueDate());
                    return summary;
                })
                .collect(Collectors.toList());
    }
}
