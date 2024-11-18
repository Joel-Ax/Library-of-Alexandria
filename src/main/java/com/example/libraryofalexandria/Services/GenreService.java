package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Book;
import com.example.libraryofalexandria.Models.Genre;
import com.example.libraryofalexandria.Repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService {

    private GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    public Genre updateGenre(Long id, Genre genre) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        Optional.ofNullable(genre.getName()).ifPresent(existingGenre::setName);
        return genreRepository.save(existingGenre);
    }

    public Genre deleteGenre(Long id) {
        Genre genreToDelete = genreRepository.findById(id).orElse(null);
        if (genreToDelete != null) {
            genreRepository.delete(genreToDelete);
        }
        return genreToDelete;
    }

}
