package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.domain.LocalizedEntity;
import com.bonlimousin.content.service.LocalizedService;
import com.bonlimousin.content.web.rest.errors.BadRequestAlertException;
import com.bonlimousin.content.service.dto.LocalizedCriteria;
import com.bonlimousin.content.service.LocalizedQueryService;

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
 * REST controller for managing {@link com.bonlimousin.content.domain.LocalizedEntity}.
 */
@RestController
@RequestMapping("/api")
public class LocalizedResource {

    private final Logger log = LoggerFactory.getLogger(LocalizedResource.class);

    private static final String ENTITY_NAME = "bonContentServiceLocalized";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocalizedService localizedService;

    private final LocalizedQueryService localizedQueryService;

    public LocalizedResource(LocalizedService localizedService, LocalizedQueryService localizedQueryService) {
        this.localizedService = localizedService;
        this.localizedQueryService = localizedQueryService;
    }

    /**
     * {@code POST  /localizeds} : Create a new localized.
     *
     * @param localizedEntity the localizedEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new localizedEntity, or with status {@code 400 (Bad Request)} if the localized has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/localizeds")
    public ResponseEntity<LocalizedEntity> createLocalized(@Valid @RequestBody LocalizedEntity localizedEntity) throws URISyntaxException {
        log.debug("REST request to save Localized : {}", localizedEntity);
        if (localizedEntity.getId() != null) {
            throw new BadRequestAlertException("A new localized cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LocalizedEntity result = localizedService.save(localizedEntity);
        return ResponseEntity.created(new URI("/api/localizeds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /localizeds} : Updates an existing localized.
     *
     * @param localizedEntity the localizedEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated localizedEntity,
     * or with status {@code 400 (Bad Request)} if the localizedEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the localizedEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/localizeds")
    public ResponseEntity<LocalizedEntity> updateLocalized(@Valid @RequestBody LocalizedEntity localizedEntity) throws URISyntaxException {
        log.debug("REST request to update Localized : {}", localizedEntity);
        if (localizedEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LocalizedEntity result = localizedService.save(localizedEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, localizedEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /localizeds} : get all the localizeds.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of localizeds in body.
     */
    @GetMapping("/localizeds")
    public ResponseEntity<List<LocalizedEntity>> getAllLocalizeds(LocalizedCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Localizeds by criteria: {}", criteria);
        Page<LocalizedEntity> page = localizedQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /localizeds/count} : count all the localizeds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/localizeds/count")
    public ResponseEntity<Long> countLocalizeds(LocalizedCriteria criteria) {
        log.debug("REST request to count Localizeds by criteria: {}", criteria);
        return ResponseEntity.ok().body(localizedQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /localizeds/:id} : get the "id" localized.
     *
     * @param id the id of the localizedEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the localizedEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/localizeds/{id}")
    public ResponseEntity<LocalizedEntity> getLocalized(@PathVariable Long id) {
        log.debug("REST request to get Localized : {}", id);
        Optional<LocalizedEntity> localizedEntity = localizedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(localizedEntity);
    }

    /**
     * {@code DELETE  /localizeds/:id} : delete the "id" localized.
     *
     * @param id the id of the localizedEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/localizeds/{id}")
    public ResponseEntity<Void> deleteLocalized(@PathVariable Long id) {
        log.debug("REST request to delete Localized : {}", id);
        localizedService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/localizeds?query=:query} : search for the localized corresponding
     * to the query.
     *
     * @param query the query of the localized search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/localizeds")
    public ResponseEntity<List<LocalizedEntity>> searchLocalizeds(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Localizeds for query {}", query);
        Page<LocalizedEntity> page = localizedService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
