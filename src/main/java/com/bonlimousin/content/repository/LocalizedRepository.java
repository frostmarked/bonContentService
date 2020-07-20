package com.bonlimousin.content.repository;

import com.bonlimousin.content.domain.LocalizedEntity;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the LocalizedEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocalizedRepository extends JpaRepository<LocalizedEntity, Long>, JpaSpecificationExecutor<LocalizedEntity> {
}
