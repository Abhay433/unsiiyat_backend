package com.unsiiyat.backend.modules.authorDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDetailRepository extends JpaRepository<AuthorDetailEntity, Long>, JpaSpecificationExecutor<AuthorDetailEntity> {

    @Query("SELECT a FROM AuthorDetailEntity a WHERE a.author.id = :authorId")
    List<AuthorDetailEntity> findByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT a FROM AuthorDetailEntity a WHERE a.author.id = :authorId AND a.script.id = :scriptId")
    Optional<AuthorDetailEntity> findByAuthorIdAndScriptId(@Param("authorId") Long authorId, @Param("scriptId") Long scriptId);

    @Query("SELECT COUNT(a) > 0 FROM AuthorDetailEntity a WHERE a.author.id = :authorId AND a.script.id = :scriptId")
    boolean existsByAuthorIdAndScriptId(@Param("authorId") Long authorId, @Param("scriptId") Long scriptId);
}
