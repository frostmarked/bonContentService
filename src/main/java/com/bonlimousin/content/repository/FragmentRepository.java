package com.bonlimousin.content.repository;

import com.bonlimousin.content.domain.FragmentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the FragmentEntity entity.
 */
@Repository
public interface FragmentRepository extends JpaRepository<FragmentEntity, Long>, JpaSpecificationExecutor<FragmentEntity> {

    @Query(value = "select distinct fragment from FragmentEntity fragment left join fetch fragment.tags",
        countQuery = "select count(distinct fragment) from FragmentEntity fragment")
    Page<FragmentEntity> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct fragment from FragmentEntity fragment left join fetch fragment.tags")
    List<FragmentEntity> findAllWithEagerRelationships();

    @Query("select fragment from FragmentEntity fragment left join fetch fragment.tags where fragment.id =:id")
    Optional<FragmentEntity> findOneWithEagerRelationships(@Param("id") Long id);
}
