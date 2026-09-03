package com.unsiiyat.backend.modules.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long>, JpaSpecificationExecutor<ContentEntity> {

    List<ContentEntity> findByGenreId(Long genreId);

    List<ContentEntity> findByAuthorId(Long authorId);
}
