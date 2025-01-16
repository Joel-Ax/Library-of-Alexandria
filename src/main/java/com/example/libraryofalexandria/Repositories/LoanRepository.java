package com.example.libraryofalexandria.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.libraryofalexandria.DTO.UserDTO;
import com.example.libraryofalexandria.Models.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserAndReturnedDateIsNull(UserDTO user);  // Fetch loans where returned is NULL (not returned)
}



