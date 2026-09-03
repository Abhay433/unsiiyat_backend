package com.unsiiyat.backend.modules.genre;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long>, JpaSpecificationExecutor<GenreEntity> {

    Optional<GenreEntity> findBySlug(String slug);

    Optional<GenreEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}
