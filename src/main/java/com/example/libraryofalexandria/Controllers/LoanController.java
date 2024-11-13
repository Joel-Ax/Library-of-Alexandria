package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Services.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> body) {
        String bookTitle = (String) body.get("bookTitle");
        Long userId = ((Number) body.get("userId")).longValue();

        Book book = bookService.getBookByTitle(bookTitle);
        User user = userService.getUserById(userId);

        try {
            Loan loan = loanService.borrowBook(book, user);

            LocalDate loanDate = LocalDate.now();
            LocalDate dueDate = loanDate.plus(14, ChronoUnit.DAYS);
            loan.setLoanDate(loanDate);
            loan.setDueDate(dueDate);

            // Use LinkedHashMap to ensure order
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





    @PostMapping("/return")
    public ResponseEntity<Loan> returnBook(@RequestParam Long loanId) {
        Loan loan = loanService.returnBook(loanId);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/onLoan/{id}")
    public ResponseEntity<List<Book>> getActiveBorrowedBooks(@PathVariable Long id) {
        // Get the user by ID
        User user = userService.getUserById(id);

        // Retrieve all active loans for the user (where the book is not yet returned)
        List<Loan> activeLoans = loanService.getActiveLoans(user);

        // Extract books from the active loans (only books that are not returned)
        List<Book> borrowedBooks = activeLoans.stream()
                .filter(loan -> Boolean.FALSE.equals(loan.getReturned()))// Filter out books that have been returned
                .map(Loan::getBook) // Get the book from the loan
                .collect(Collectors.toList()); // Collect into a list of books


        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("/onLoan/{id}/dueDates")
    public ResponseEntity<List<Map<String, Object>>> getActiveBorrowedBooksSummary(@PathVariable Long id) {
        // Get the user by ID
        User user = userService.getUserById(id);

        // Retrieve all active loans for the user (where the book is not yet returned)
        List<Loan> activeLoans = loanService.getActiveLoans(user);

        // Extract title and dueDate from active loans
        List<Map<String, Object>> borrowedBooksSummary = activeLoans.stream()
                .filter(loan -> Boolean.FALSE.equals(loan.getReturned())) // Only active loans (not returned)
                .map(loan -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("title", loan.getBook().getTitle()); // Get the book title
                    summary.put("dueDate", loan.getDueDate()); // Get the due date
                    return summary;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(borrowedBooksSummary);
    }

}