package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Services.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.libraryofalexandria.Services.BookService;
import com.example.libraryofalexandria.Services.UserService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final UserService userService;

    public LoanController(LoanService loanService, BookService bookService, UserService userService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> body) {
        String bookTitle = (String) body.get("bookTitle");
        Long userId = ((Number) body.get("userId")).longValue();

        Book book = bookService.getBookByTitle(bookTitle);
        User user = userService.getUserById(userId);

        try {
            Loan loan = loanService.borrowBook(book, user);

            LocalDate loanDate = LocalDate.now();
            LocalDate dueDate = loanDate.plusDays(30);
            loan.setLoanDate(loanDate);
            loan.setDueDate(dueDate);

            // LinkedHashMap skriver ut i rätt ordning
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("title", book.getTitle());
            response.put("bookId", book.getId());
            response.put("loanDate", loanDate);
            response.put("dueDate", dueDate);
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The book is already borrowed");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Object> body) {
        String bookTitle = (String) body.get("bookTitle");
        Long userId = ((Number) body.get("userId")).longValue();

        Book book = bookService.getBookByTitle(bookTitle);
        User user = userService.getUserById(userId);

        try {
            // Filtrerar fram aktiva lån
            Loan activeLoan = loanService.getActiveLoans(user).stream()
                    .filter(loan -> loan.getBook().equals(book) && !loan.getReturned())
                    .findFirst()
                    .orElse(null);

            Loan updatedLoan = loanService.returnBook(activeLoan.getId());

            book.setAvailable(true);
            bookService.updateBook(book);

            return new ResponseEntity<>("Book returned successfully", HttpStatus.OK);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while returning the book");
        }
    }


    @GetMapping("/onLoan/{id}")
    public ResponseEntity<List<Book>> getActiveBorrowedBooks(@PathVariable Long id) {
        User user = userService.getUserById(id);

        List<Loan> activeLoans = loanService.getActiveLoans(user);

        // Filtrerar fram böcker från aktiv lån
        List<Book> borrowedBooks = activeLoans.stream()
                .filter(loan -> Boolean.FALSE.equals(loan.getReturned()))
                .map(Loan::getBook)
                .collect(Collectors.toList());


        return ResponseEntity.ok(borrowedBooks);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/onLoan/{id}/dueDates")
    public ResponseEntity<List<Map<String, Object>>> getActiveBorrowedBooksSummary(@PathVariable Long id) {
        User user = userService.getUserById(id);

        List<Loan> activeLoans = loanService.getActiveLoans(user);

        // Filtrera fram titel och dueDate
        List<Map<String, Object>> borrowedBooksSummary = activeLoans.stream()
                .filter(loan -> Boolean.FALSE.equals(loan.getReturned()))
                .map(loan -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("title", loan.getBook().getTitle());
                    summary.put("dueDate", loan.getDueDate());
                    return summary;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(borrowedBooksSummary);
    }

}
