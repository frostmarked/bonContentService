package com.bonlimousin.content.service;

import com.bonlimousin.content.domain.StoryEntity;
import com.bonlimousin.content.repository.StoryRepository;
import com.bonlimousin.content.repository.search.StorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link StoryEntity}.
 */
@Service
@Transactional
public class StoryService {

    private final Logger log = LoggerFactory.getLogger(StoryService.class);

    private final StoryRepository storyRepository;

    private final StorySearchRepository storySearchRepository;

    public StoryService(StoryRepository storyRepository, StorySearchRepository storySearchRepository) {
        this.storyRepository = storyRepository;
        this.storySearchRepository = storySearchRepository;
    }

    /**
     * Save a story.
     *
     * @param storyEntity the entity to save.
     * @return the persisted entity.
     */
    public StoryEntity save(StoryEntity storyEntity) {
        log.debug("Request to save Story : {}", storyEntity);
        StoryEntity result = storyRepository.save(storyEntity);
        storySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the stories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StoryEntity> findAll(Pageable pageable) {
        log.debug("Request to get all Stories");
        return storyRepository.findAll(pageable);
    }


    /**
     * Get one story by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoryEntity> findOne(Long id) {
        log.debug("Request to get Story : {}", id);
        return storyRepository.findById(id);
    }

    /**
     * Delete the story by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Story : {}", id);
        storyRepository.deleteById(id);
        storySearchRepository.deleteById(id);
    }

    /**
     * Search for the story corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StoryEntity> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Stories for query {}", query);
        return storySearchRepository.search(queryStringQuery(query), pageable);    }
}
