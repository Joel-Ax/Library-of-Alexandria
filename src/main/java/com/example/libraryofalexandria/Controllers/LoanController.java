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

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> body) {
        try {
            String bookTitle = (String) body.get("bookTitle");
            Long userId = ((Number) body.get("userId")).longValue();

            Book book = bookService.getBookByTitle(bookTitle);
            User user = userService.getUserById(userId);

            Loan loan = loanService.borrowBook(book, user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("title", book.getTitle());
            response.put("bookId", book.getId());
            response.put("loanDate", loan.getLoanDate());
            response.put("dueDate", loan.getDueDate());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Object> body) {
        try {
            String bookTitle = (String) body.get("bookTitle");
            Long userId = ((Number) body.get("userId")).longValue();

            Book book = bookService.getBookByTitle(bookTitle);
            User user = userService.getUserById(userId);

            loanService.returnBook(book, user);
            return ResponseEntity.ok("Book returned successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/onLoan/{id}")
    public ResponseEntity<List<Book>> getActiveBorrowedBooks(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(loanService.getBorrowedBooks(user));
    }

    @GetMapping("/onLoan/{id}/dueDates")
    public ResponseEntity<List<Map<String, Object>>> getActiveBorrowedBooksSummary(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(loanService.getBorrowedBooksSummary(user));
    }
}

