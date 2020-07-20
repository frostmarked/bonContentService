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

import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.domain.*; // for static metamodels
import com.bonlimousin.content.repository.FragmentRepository;
import com.bonlimousin.content.repository.search.FragmentSearchRepository;
import com.bonlimousin.content.service.dto.FragmentCriteria;

/**
 * Service for executing complex queries for {@link FragmentEntity} entities in the database.
 * The main input is a {@link FragmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FragmentEntity} or a {@link Page} of {@link FragmentEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FragmentQueryService extends QueryService<FragmentEntity> {

    private final Logger log = LoggerFactory.getLogger(FragmentQueryService.class);

    private final FragmentRepository fragmentRepository;

    private final FragmentSearchRepository fragmentSearchRepository;

    public FragmentQueryService(FragmentRepository fragmentRepository, FragmentSearchRepository fragmentSearchRepository) {
        this.fragmentRepository = fragmentRepository;
        this.fragmentSearchRepository = fragmentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FragmentEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FragmentEntity> findByCriteria(FragmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FragmentEntity> specification = createSpecification(criteria);
        return fragmentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link FragmentEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FragmentEntity> findByCriteria(FragmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FragmentEntity> specification = createSpecification(criteria);
        return fragmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FragmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FragmentEntity> specification = createSpecification(criteria);
        return fragmentRepository.count(specification);
    }

    /**
     * Function to convert {@link FragmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FragmentEntity> createSpecification(FragmentCriteria criteria) {
        Specification<FragmentEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FragmentEntity_.id));
            }
            if (criteria.getTemplate() != null) {
                specification = specification.and(buildSpecification(criteria.getTemplate(), FragmentEntity_.template));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), FragmentEntity_.name));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), FragmentEntity_.title));
            }
            if (criteria.getIngress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIngress(), FragmentEntity_.ingress));
            }
            if (criteria.getCaption() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCaption(), FragmentEntity_.caption));
            }
            if (criteria.getWidth() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWidth(), FragmentEntity_.width));
            }
            if (criteria.getHeight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHeight(), FragmentEntity_.height));
            }
            if (criteria.getOrderNo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderNo(), FragmentEntity_.orderNo));
            }
            if (criteria.getVisibility() != null) {
                specification = specification.and(buildSpecification(criteria.getVisibility(), FragmentEntity_.visibility));
            }
            if (criteria.getLocalizedFragmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getLocalizedFragmentId(),
                    root -> root.join(FragmentEntity_.localizedFragments, JoinType.LEFT).get(LocalizedEntity_.id)));
            }
            if (criteria.getTagId() != null) {
                specification = specification.and(buildSpecification(criteria.getTagId(),
                    root -> root.join(FragmentEntity_.tags, JoinType.LEFT).get(TagEntity_.id)));
            }
            if (criteria.getStoryId() != null) {
                specification = specification.and(buildSpecification(criteria.getStoryId(),
                    root -> root.join(FragmentEntity_.story, JoinType.LEFT).get(StoryEntity_.id)));
            }
        }
        return specification;
    }
}
