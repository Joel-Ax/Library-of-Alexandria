package com.example.libraryofalexandria.Repositories;

import com.example.libraryofalexandria.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
