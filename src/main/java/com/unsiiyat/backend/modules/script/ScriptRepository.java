package com.unsiiyat.backend.modules.script;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<ScriptEntity, Long>, JpaSpecificationExecutor<ScriptEntity> {

    Optional<ScriptEntity> findByCode(String code);

    Optional<ScriptEntity> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}
