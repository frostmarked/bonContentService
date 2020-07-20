package com.bonlimousin.content.repository;

import com.bonlimousin.content.domain.TagEntity;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the TagEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long>, JpaSpecificationExecutor<TagEntity> {
}
