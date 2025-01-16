package com.example.libraryofalexandria.Services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.libraryofalexandria.DTO.UserDTO;
import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.LoanRepository;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan borrowBook(Book book, UserDTO user) {
        if (book.getAvailable() == null || !book.getAvailable()) {
            throw new RuntimeException("The book is already borrowed");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(convertToUser(user));
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30));
        loan.setReturned(false);

        loanRepository.save(loan);

        book.setAvailable(false);
        return loan;
    }

    public List<Loan> getActiveLoans(UserDTO user) {
        return loanRepository.findByUserAndReturnedDateIsNull(user);
    }

    public Loan returnBook(Book book, UserDTO user) {
        Loan activeLoan = getActiveLoanByBookAndUser(book, user);
        activeLoan.setReturned(true);
        activeLoan.setReturnedDate(LocalDate.now());
        loanRepository.save(activeLoan);

        book.setAvailable(true);
        return activeLoan;
    }

    public Loan getActiveLoanByBookAndUser(Book book, UserDTO user) {
        return getActiveLoans(user).stream()
                .filter(loan -> loan.getBook().equals(book))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active loan found for this book and user"));
    }

    public List<Book> getBorrowedBooks(UserDTO user) {
        return getActiveLoans(user).stream()
                .map(Loan::getBook)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getBorrowedBooksSummary(UserDTO user) {
        return getActiveLoans(user).stream()
                .map(loan -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("title", loan.getBook().getTitle());
                    summary.put("dueDate", loan.getDueDate());
                    return summary;
                })
                .collect(Collectors.toList());
    }

    private User convertToUser(UserDTO user) {
        User convertedUser = new User();
        convertedUser.setId(user.getId());
        convertedUser.setFirstName(user.getFirstName());
        convertedUser.setLastName(user.getLastName());
        convertedUser.setEmail(user.getEmail());
        convertedUser.setMemberNumber(user.getMemberNumber());
        return convertedUser;
    }
}
