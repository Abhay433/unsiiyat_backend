package com.unsiiyat.backend.modules.contextText;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentTextRepository extends JpaRepository<ContentTextEntity, Long>, JpaSpecificationExecutor<ContentTextEntity> {

    List<ContentTextEntity> findByContentId(Long contentId);

    Optional<ContentTextEntity> findByContentIdAndScriptId(Long contentId, Long scriptId);

    boolean existsByContentIdAndScriptId(Long contentId, Long scriptId);
}
