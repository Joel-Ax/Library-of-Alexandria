package com.example.libraryofalexandria.Repositories;

import com.example.libraryofalexandria.Models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

    // Lägg till metoden för att kontrollera om användarnamnet finns
    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE Admin a SET a.failedAttempts = :failedAttempts WHERE a.username = :username")
    void updateFailedAttempts(@Param("failedAttempts") int failedAttempts, @Param("username") String username);



    @Modifying
    @Query("UPDATE Admin a SET a.accountLocked = ?1, a.lockTime = ?2 WHERE a.username = ?3")
    void updateLockStatus(boolean locked, Long lockTime, String username);
}
