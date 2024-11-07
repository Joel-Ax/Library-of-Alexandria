package com.example.libraryofalexandria.Repositories;

import com.example.libraryofalexandria.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    Book findByTitle(String title);

    Book findByAuthor(String author);


}
