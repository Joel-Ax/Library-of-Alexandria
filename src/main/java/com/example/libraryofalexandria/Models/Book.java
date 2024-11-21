package com.example.libraryofalexandria.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "publication_year", nullable = false)
    private int publication_year;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name = "available", columnDefinition = "TINYINT default 1")
    private Boolean available;


    @ManyToMany
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_books_genres_book")),
            inverseJoinColumns = @JoinColumn(name = "genre_id", foreignKey = @ForeignKey(name = "fk_books_genres_genre")))
    private Set<Genre> genres = new HashSet<>();


    public boolean hasGenre(String genreName) {
        for (Genre genre : genres) {
            if (genre.getName().equalsIgnoreCase(genreName)) {
                return true;
            }
        }
        return false;
    }

    /*public boolean hasNoGenre(Book book) {
        return book.getGenres() == null || book.getGenres().isEmpty();
    }*/

}
