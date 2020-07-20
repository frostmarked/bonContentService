package com.bonlimousin.content.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.bonlimousin.content.domain.StoryEntity;
import com.bonlimousin.content.domain.*; // for static metamodels
import com.bonlimousin.content.repository.StoryRepository;
import com.bonlimousin.content.repository.search.StorySearchRepository;
import com.bonlimousin.content.service.dto.StoryCriteria;

/**
 * Service for executing complex queries for {@link StoryEntity} entities in the database.
 * The main input is a {@link StoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StoryEntity} or a {@link Page} of {@link StoryEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StoryQueryService extends QueryService<StoryEntity> {

    private final Logger log = LoggerFactory.getLogger(StoryQueryService.class);

    private final StoryRepository storyRepository;

    private final StorySearchRepository storySearchRepository;

    public StoryQueryService(StoryRepository storyRepository, StorySearchRepository storySearchRepository) {
        this.storyRepository = storyRepository;
        this.storySearchRepository = storySearchRepository;
    }

    /**
     * Return a {@link List} of {@link StoryEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StoryEntity> findByCriteria(StoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StoryEntity> specification = createSpecification(criteria);
        return storyRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link StoryEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StoryEntity> findByCriteria(StoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StoryEntity> specification = createSpecification(criteria);
        return storyRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StoryEntity> specification = createSpecification(criteria);
        return storyRepository.count(specification);
    }

    /**
     * Function to convert {@link StoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StoryEntity> createSpecification(StoryCriteria criteria) {
        Specification<StoryEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StoryEntity_.id));
            }
            if (criteria.getCategory() != null) {
                specification = specification.and(buildSpecification(criteria.getCategory(), StoryEntity_.category));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), StoryEntity_.name));
            }
            if (criteria.getVisibility() != null) {
                specification = specification.and(buildSpecification(criteria.getVisibility(), StoryEntity_.visibility));
            }
            if (criteria.getFragmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getFragmentId(),
                    root -> root.join(StoryEntity_.fragments, JoinType.LEFT).get(FragmentEntity_.id)));
            }
        }
        return specification;
    }
}
