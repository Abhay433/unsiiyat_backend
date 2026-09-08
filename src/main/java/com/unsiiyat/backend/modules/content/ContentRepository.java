package com.unsiiyat.backend.modules.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long>, JpaSpecificationExecutor<ContentEntity> {

    List<ContentEntity> findByGenreId(Long genreId);

    List<ContentEntity> findByAuthorId(Long authorId);

    @Query("SELECT c.id FROM ContentEntity c WHERE c.genre.id = :genreId ORDER BY c.id ASC")
    List<Long> findIdsByGenreId(@Param("genreId") Long genreId);

    @Query("SELECT c.id FROM ContentEntity c ORDER BY c.id ASC")
    List<Long> findAllIds();

    List<ContentEntity> findByIsSelectedTrue();

    List<ContentEntity> findByGenreIdAndIsSelectedTrue(Long genreId, Pageable pageable);

    List<ContentEntity> findByGenreId(Long genreId, Pageable pageable);

    long countByGenreIdAndIsSelectedTrue(Long genreId);
}
