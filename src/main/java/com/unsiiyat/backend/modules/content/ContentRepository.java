package com.unsiiyat.backend.modules.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long>, JpaSpecificationExecutor<ContentEntity> {

    List<ContentEntity> findByGenreId(Long genreId);

    List<ContentEntity> findByAuthorId(Long authorId);

    @Query("SELECT c.id FROM ContentEntity c WHERE c.genre.id = :genreId ORDER BY c.id ASC")
    List<Long> findIdsByGenreId(@Param("genreId") Long genreId);

    @Query("SELECT c.id FROM ContentEntity c ORDER BY c.id ASC")
    List<Long> findAllIds();
}
