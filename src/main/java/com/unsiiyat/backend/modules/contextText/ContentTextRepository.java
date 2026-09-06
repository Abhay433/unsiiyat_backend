package com.unsiiyat.backend.modules.contextText;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentTextRepository extends JpaRepository<ContentTextEntity, Long>, JpaSpecificationExecutor<ContentTextEntity> {

    @Query("SELECT c FROM ContentTextEntity c WHERE c.content.id = :contentId")
    List<ContentTextEntity> findByContentId(@Param("contentId") Long contentId);

    @Query("SELECT c FROM ContentTextEntity c WHERE c.content.id = :contentId AND c.script.id = :scriptId")
    Optional<ContentTextEntity> findByContentIdAndScriptId(@Param("contentId") Long contentId, @Param("scriptId") Long scriptId);

    @Query("SELECT COUNT(c) > 0 FROM ContentTextEntity c WHERE c.content.id = :contentId AND c.script.id = :scriptId")
    boolean existsByContentIdAndScriptId(@Param("contentId") Long contentId, @Param("scriptId") Long scriptId);
}
