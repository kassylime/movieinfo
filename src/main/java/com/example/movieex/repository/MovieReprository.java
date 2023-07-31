package com.example.movieex.repository;

import com.example.movieex.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieReprository extends JpaRepository<Movie, Long> {
    Page<Movie> findByMcodeGreaterThan(Long mcode, Pageable pb);
}
