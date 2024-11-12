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
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


import java.util.List;

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
    public ResponseEntity<Loan> borrowBook(@RequestBody Map<String, Object> body) {
        // Extract bookTitle and userId directly from the request body
        String bookTitle = (String) body.get("bookTitle");
        Long userId = ((Number) body.get("userId")).longValue();

        // Get the book and user objects from the services
        Book book = bookService.getBookByTitle(bookTitle);
        User user = userService.getUserById(userId);

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plus(14, ChronoUnit.DAYS);

        // Borrow the book and create the loan
        Loan loan = loanService.borrowBook(book, user);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);

        // Return the created loan object with a CREATED status
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }



    @PostMapping("/return")
    public ResponseEntity<Loan> returnBook(@RequestParam Long loanId) {
        Loan loan = loanService.returnBook(loanId);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loan>> getActiveLoans(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Loan> loans = loanService.getActiveLoans(user);
        return ResponseEntity.ok(loans);
    }
}
