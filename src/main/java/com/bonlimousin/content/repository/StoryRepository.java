package com.bonlimousin.content.repository;

import com.bonlimousin.content.domain.StoryEntity;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the StoryEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoryRepository extends JpaRepository<StoryEntity, Long>, JpaSpecificationExecutor<StoryEntity> {
}
