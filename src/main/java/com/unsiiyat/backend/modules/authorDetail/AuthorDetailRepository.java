package com.unsiiyat.backend.modules.authorDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorDetailRepository extends JpaRepository<AuthorDetailEntity, Long>, JpaSpecificationExecutor<AuthorDetailEntity> {

    List<AuthorDetailEntity> findByAuthorId(Long authorId);

    Optional<AuthorDetailEntity> findByAuthorIdAndScriptId(Long authorId, Long scriptId);

    boolean existsByAuthorIdAndScriptId(Long authorId, Long scriptId);
}
