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

import com.bonlimousin.content.domain.LocalizedEntity;
import com.bonlimousin.content.domain.*; // for static metamodels
import com.bonlimousin.content.repository.LocalizedRepository;
import com.bonlimousin.content.repository.search.LocalizedSearchRepository;
import com.bonlimousin.content.service.dto.LocalizedCriteria;

/**
 * Service for executing complex queries for {@link LocalizedEntity} entities in the database.
 * The main input is a {@link LocalizedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LocalizedEntity} or a {@link Page} of {@link LocalizedEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LocalizedQueryService extends QueryService<LocalizedEntity> {

    private final Logger log = LoggerFactory.getLogger(LocalizedQueryService.class);

    private final LocalizedRepository localizedRepository;

    private final LocalizedSearchRepository localizedSearchRepository;

    public LocalizedQueryService(LocalizedRepository localizedRepository, LocalizedSearchRepository localizedSearchRepository) {
        this.localizedRepository = localizedRepository;
        this.localizedSearchRepository = localizedSearchRepository;
    }

    /**
     * Return a {@link List} of {@link LocalizedEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LocalizedEntity> findByCriteria(LocalizedCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LocalizedEntity> specification = createSpecification(criteria);
        return localizedRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LocalizedEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LocalizedEntity> findByCriteria(LocalizedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LocalizedEntity> specification = createSpecification(criteria);
        return localizedRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LocalizedCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LocalizedEntity> specification = createSpecification(criteria);
        return localizedRepository.count(specification);
    }

    /**
     * Function to convert {@link LocalizedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LocalizedEntity> createSpecification(LocalizedCriteria criteria) {
        Specification<LocalizedEntity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), LocalizedEntity_.id));
            }
            if (criteria.geti18n() != null) {
                specification = specification.and(buildStringSpecification(criteria.geti18n(), LocalizedEntity_.i18n));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), LocalizedEntity_.title));
            }
            if (criteria.getIngress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIngress(), LocalizedEntity_.ingress));
            }
            if (criteria.getCaption() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCaption(), LocalizedEntity_.caption));
            }
            if (criteria.getVisibility() != null) {
                specification = specification.and(buildSpecification(criteria.getVisibility(), LocalizedEntity_.visibility));
            }
            if (criteria.getFragmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getFragmentId(),
                    root -> root.join(LocalizedEntity_.fragment, JoinType.LEFT).get(FragmentEntity_.id)));
            }
        }
        return specification;
    }
}
