package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.service.FragmentService;
import com.bonlimousin.content.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.content.service.dto.FragmentCriteria;
import com.bonlimousin.content.service.FragmentQueryService;

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
 * REST controller for managing {@link com.bonlimousin.content.domain.FragmentEntity}.
 */
@RestController
@RequestMapping("/api")
public class FragmentResource {

    private final Logger log = LoggerFactory.getLogger(FragmentResource.class);

    private static final String ENTITY_NAME = "bonContentServiceFragment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FragmentService fragmentService;

    private final FragmentQueryService fragmentQueryService;

    public FragmentResource(FragmentService fragmentService, FragmentQueryService fragmentQueryService) {
        this.fragmentService = fragmentService;
        this.fragmentQueryService = fragmentQueryService;
    }

    /**
     * {@code POST  /fragments} : Create a new fragment.
     *
     * @param fragmentEntity the fragmentEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fragmentEntity, or with status {@code 400 (Bad Request)} if the fragment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fragments")
    public ResponseEntity<FragmentEntity> createFragment(@Valid @RequestBody FragmentEntity fragmentEntity) throws URISyntaxException {
        log.debug("REST request to save Fragment : {}", fragmentEntity);
        if (fragmentEntity.getId() != null) {
            throw new BadRequestAlertException("A new fragment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FragmentEntity result = fragmentService.save(fragmentEntity);
        return ResponseEntity.created(new URI("/api/fragments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fragments} : Updates an existing fragment.
     *
     * @param fragmentEntity the fragmentEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fragmentEntity,
     * or with status {@code 400 (Bad Request)} if the fragmentEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fragmentEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fragments")
    public ResponseEntity<FragmentEntity> updateFragment(@Valid @RequestBody FragmentEntity fragmentEntity) throws URISyntaxException {
        log.debug("REST request to update Fragment : {}", fragmentEntity);
        if (fragmentEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FragmentEntity result = fragmentService.save(fragmentEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fragmentEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /fragments} : get all the fragments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fragments in body.
     */
    @GetMapping("/fragments")
    public ResponseEntity<List<FragmentEntity>> getAllFragments(FragmentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Fragments by criteria: {}", criteria);
        Page<FragmentEntity> page = fragmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fragments/count} : count all the fragments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/fragments/count")
    public ResponseEntity<Long> countFragments(FragmentCriteria criteria) {
        log.debug("REST request to count Fragments by criteria: {}", criteria);
        return ResponseEntity.ok().body(fragmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /fragments/:id} : get the "id" fragment.
     *
     * @param id the id of the fragmentEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fragmentEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fragments/{id}")
    public ResponseEntity<FragmentEntity> getFragment(@PathVariable Long id) {
        log.debug("REST request to get Fragment : {}", id);
        Optional<FragmentEntity> fragmentEntity = fragmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fragmentEntity);
    }

    /**
     * {@code DELETE  /fragments/:id} : delete the "id" fragment.
     *
     * @param id the id of the fragmentEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fragments/{id}")
    public ResponseEntity<Void> deleteFragment(@PathVariable Long id) {
        log.debug("REST request to delete Fragment : {}", id);
        fragmentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/fragments?query=:query} : search for the fragment corresponding
     * to the query.
     *
     * @param query the query of the fragment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/fragments")
    public ResponseEntity<List<FragmentEntity>> searchFragments(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Fragments for query {}", query);
        Page<FragmentEntity> page = fragmentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
