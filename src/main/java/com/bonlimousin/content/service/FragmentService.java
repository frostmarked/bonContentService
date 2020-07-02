package com.bonlimousin.content.service;

import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.repository.FragmentRepository;
import com.bonlimousin.content.repository.search.FragmentSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link FragmentEntity}.
 */
@Service
@Transactional
public class FragmentService {

    private final Logger log = LoggerFactory.getLogger(FragmentService.class);

    private final FragmentRepository fragmentRepository;

    private final FragmentSearchRepository fragmentSearchRepository;

    public FragmentService(FragmentRepository fragmentRepository, FragmentSearchRepository fragmentSearchRepository) {
        this.fragmentRepository = fragmentRepository;
        this.fragmentSearchRepository = fragmentSearchRepository;
    }

    /**
     * Save a fragment.
     *
     * @param fragmentEntity the entity to save.
     * @return the persisted entity.
     */
    public FragmentEntity save(FragmentEntity fragmentEntity) {
        log.debug("Request to save Fragment : {}", fragmentEntity);
        FragmentEntity result = fragmentRepository.save(fragmentEntity);
        fragmentSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the fragments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FragmentEntity> findAll(Pageable pageable) {
        log.debug("Request to get all Fragments");
        return fragmentRepository.findAll(pageable);
    }


    /**
     * Get all the fragments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<FragmentEntity> findAllWithEagerRelationships(Pageable pageable) {
        return fragmentRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one fragment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FragmentEntity> findOne(Long id) {
        log.debug("Request to get Fragment : {}", id);
        return fragmentRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the fragment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Fragment : {}", id);
        fragmentRepository.deleteById(id);
        fragmentSearchRepository.deleteById(id);
    }

    /**
     * Search for the fragment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FragmentEntity> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Fragments for query {}", query);
        return fragmentSearchRepository.search(queryStringQuery(query), pageable);    }
}
