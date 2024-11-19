package com.example.libraryofalexandria.Repositories;

import com.example.libraryofalexandria.Models.Loan;
import com.example.libraryofalexandria.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserAndReturnedDateIsNull(User user);  // Fetch loans where returned is NULL (not returned)
}



