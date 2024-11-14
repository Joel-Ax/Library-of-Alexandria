package com.example.libraryofalexandria.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "genres")
    private Set<Book> books = new HashSet<>();


    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + id +
                ", name='" + name + '\'' + '}'
                ;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
