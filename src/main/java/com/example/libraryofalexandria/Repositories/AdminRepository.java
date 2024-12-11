package com.example.libraryofalexandria.Repositories;

import com.example.libraryofalexandria.Models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

    // Lägg till metoden för att kontrollera om användarnamnet finns
    boolean existsByUsername(String username);
}
