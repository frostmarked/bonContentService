package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.domain.StoryEntity;
import com.bonlimousin.content.service.StoryService;
import com.bonlimousin.content.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.content.service.dto.StoryCriteria;
import com.bonlimousin.content.service.StoryQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.bonlimousin.content.domain.StoryEntity}.
 */
@RestController
@RequestMapping("/api")
public class StoryResource {

    private final Logger log = LoggerFactory.getLogger(StoryResource.class);

    private static final String ENTITY_NAME = "bonContentServiceStory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoryService storyService;

    private final StoryQueryService storyQueryService;

    public StoryResource(StoryService storyService, StoryQueryService storyQueryService) {
        this.storyService = storyService;
        this.storyQueryService = storyQueryService;
    }

    /**
     * {@code POST  /stories} : Create a new story.
     *
     * @param storyEntity the storyEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storyEntity, or with status {@code 400 (Bad Request)} if the story has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stories")
    public ResponseEntity<StoryEntity> createStory(@Valid @RequestBody StoryEntity storyEntity) throws URISyntaxException {
        log.debug("REST request to save Story : {}", storyEntity);
        if (storyEntity.getId() != null) {
            throw new BadRequestAlertException("A new story cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StoryEntity result = storyService.save(storyEntity);
        return ResponseEntity.created(new URI("/api/stories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stories} : Updates an existing story.
     *
     * @param storyEntity the storyEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storyEntity,
     * or with status {@code 400 (Bad Request)} if the storyEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storyEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stories")
    public ResponseEntity<StoryEntity> updateStory(@Valid @RequestBody StoryEntity storyEntity) throws URISyntaxException {
        log.debug("REST request to update Story : {}", storyEntity);
        if (storyEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        StoryEntity result = storyService.save(storyEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, storyEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /stories} : get all the stories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stories in body.
     */
    @GetMapping("/stories")
    public ResponseEntity<List<StoryEntity>> getAllStories(StoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Stories by criteria: {}", criteria);
        Page<StoryEntity> page = storyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stories/count} : count all the stories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stories/count")
    public ResponseEntity<Long> countStories(StoryCriteria criteria) {
        log.debug("REST request to count Stories by criteria: {}", criteria);
        return ResponseEntity.ok().body(storyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stories/:id} : get the "id" story.
     *
     * @param id the id of the storyEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storyEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stories/{id}")
    public ResponseEntity<StoryEntity> getStory(@PathVariable Long id) {
        log.debug("REST request to get Story : {}", id);
        Optional<StoryEntity> storyEntity = storyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storyEntity);
    }

    /**
     * {@code DELETE  /stories/:id} : delete the "id" story.
     *
     * @param id the id of the storyEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stories/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        log.debug("REST request to delete Story : {}", id);
        storyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/stories?query=:query} : search for the story corresponding
     * to the query.
     *
     * @param query the query of the story search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/stories")
    public ResponseEntity<List<StoryEntity>> searchStories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Stories for query {}", query);
        Page<StoryEntity> page = storyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
