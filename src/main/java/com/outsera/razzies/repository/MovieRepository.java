package com.outsera.razzies.repository;

import com.outsera.razzies.entity.MovieEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    @EntityGraph(attributePaths = "produtores")
    List<MovieEntity> findByVencedorTrueOrderByAnoPremiacaoAscTituloAsc();

    long countByVencedorTrue();

    @EntityGraph(attributePaths = "produtores")
    Optional<MovieEntity> findByTitulo(String titulo);
}
