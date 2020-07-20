package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.domain.TagEntity;
import com.bonlimousin.content.service.TagService;
import com.bonlimousin.content.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.content.service.dto.TagCriteria;
import com.bonlimousin.content.service.TagQueryService;

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
 * REST controller for managing {@link com.bonlimousin.content.domain.TagEntity}.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "bonContentServiceTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagService tagService;

    private final TagQueryService tagQueryService;

    public TagResource(TagService tagService, TagQueryService tagQueryService) {
        this.tagService = tagService;
        this.tagQueryService = tagQueryService;
    }

    /**
     * {@code POST  /tags} : Create a new tag.
     *
     * @param tagEntity the tagEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagEntity, or with status {@code 400 (Bad Request)} if the tag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tags")
    public ResponseEntity<TagEntity> createTag(@Valid @RequestBody TagEntity tagEntity) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tagEntity);
        if (tagEntity.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TagEntity result = tagService.save(tagEntity);
        return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tags} : Updates an existing tag.
     *
     * @param tagEntity the tagEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagEntity,
     * or with status {@code 400 (Bad Request)} if the tagEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tags")
    public ResponseEntity<TagEntity> updateTag(@Valid @RequestBody TagEntity tagEntity) throws URISyntaxException {
        log.debug("REST request to update Tag : {}", tagEntity);
        if (tagEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TagEntity result = tagService.save(tagEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagEntity>> getAllTags(TagCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tags by criteria: {}", criteria);
        Page<TagEntity> page = tagQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tags/count} : count all the tags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tags/count")
    public ResponseEntity<Long> countTags(TagCriteria criteria) {
        log.debug("REST request to count Tags by criteria: {}", criteria);
        return ResponseEntity.ok().body(tagQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tags/:id} : get the "id" tag.
     *
     * @param id the id of the tagEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tags/{id}")
    public ResponseEntity<TagEntity> getTag(@PathVariable Long id) {
        log.debug("REST request to get Tag : {}", id);
        Optional<TagEntity> tagEntity = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tagEntity);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tag.
     *
     * @param id the id of the tagEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);
        tagService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/tags?query=:query} : search for the tag corresponding
     * to the query.
     *
     * @param query the query of the tag search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/tags")
    public ResponseEntity<List<TagEntity>> searchTags(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Tags for query {}", query);
        Page<TagEntity> page = tagService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
