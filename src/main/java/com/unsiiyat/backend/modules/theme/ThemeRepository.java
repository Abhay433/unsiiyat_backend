package com.unsiiyat.backend.modules.theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<ThemeEntity, Long>, JpaSpecificationExecutor<ThemeEntity> {

    Optional<ThemeEntity> findBySlug(String slug);

    Optional<ThemeEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}
