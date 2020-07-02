package com.bonlimousin.content.service;

import com.bonlimousin.content.domain.LocalizedEntity;
import com.bonlimousin.content.repository.LocalizedRepository;
import com.bonlimousin.content.repository.search.LocalizedSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link LocalizedEntity}.
 */
@Service
@Transactional
public class LocalizedService {

    private final Logger log = LoggerFactory.getLogger(LocalizedService.class);

    private final LocalizedRepository localizedRepository;

    private final LocalizedSearchRepository localizedSearchRepository;

    public LocalizedService(LocalizedRepository localizedRepository, LocalizedSearchRepository localizedSearchRepository) {
        this.localizedRepository = localizedRepository;
        this.localizedSearchRepository = localizedSearchRepository;
    }

    /**
     * Save a localized.
     *
     * @param localizedEntity the entity to save.
     * @return the persisted entity.
     */
    public LocalizedEntity save(LocalizedEntity localizedEntity) {
        log.debug("Request to save Localized : {}", localizedEntity);
        LocalizedEntity result = localizedRepository.save(localizedEntity);
        localizedSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the localizeds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LocalizedEntity> findAll(Pageable pageable) {
        log.debug("Request to get all Localizeds");
        return localizedRepository.findAll(pageable);
    }


    /**
     * Get one localized by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LocalizedEntity> findOne(Long id) {
        log.debug("Request to get Localized : {}", id);
        return localizedRepository.findById(id);
    }

    /**
     * Delete the localized by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Localized : {}", id);
        localizedRepository.deleteById(id);
        localizedSearchRepository.deleteById(id);
    }

    /**
     * Search for the localized corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LocalizedEntity> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Localizeds for query {}", query);
        return localizedSearchRepository.search(queryStringQuery(query), pageable);    }
}
