package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.BonContentServiceApp;
import com.bonlimousin.content.domain.LocalizedEntity;
import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.repository.LocalizedRepository;
import com.bonlimousin.content.repository.search.LocalizedSearchRepository;
import com.bonlimousin.content.service.LocalizedService;
import com.bonlimousin.content.service.dto.LocalizedCriteria;
import com.bonlimousin.content.service.LocalizedQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bonlimousin.content.domain.enumeration.UserRole;
/**
 * Integration tests for the {@link LocalizedResource} REST controller.
 */
@SpringBootTest(classes = BonContentServiceApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LocalizedResourceIT {

    private static final String DEFAULT_I_18_N = "ytt";
    private static final String UPDATED_I_18_N = "oed";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_INGRESS = "AAAAAAAAAA";
    private static final String UPDATED_INGRESS = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final UserRole DEFAULT_VISIBILITY = UserRole.ROLE_ADMIN;
    private static final UserRole UPDATED_VISIBILITY = UserRole.ROLE_USER;

    @Autowired
    private LocalizedRepository localizedRepository;

    @Autowired
    private LocalizedService localizedService;

    /**
     * This repository is mocked in the com.bonlimousin.content.repository.search test package.
     *
     * @see com.bonlimousin.content.repository.search.LocalizedSearchRepositoryMockConfiguration
     */
    @Autowired
    private LocalizedSearchRepository mockLocalizedSearchRepository;

    @Autowired
    private LocalizedQueryService localizedQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocalizedMockMvc;

    private LocalizedEntity localizedEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocalizedEntity createEntity(EntityManager em) {
        LocalizedEntity localizedEntity = new LocalizedEntity()
            .i18n(DEFAULT_I_18_N)
            .title(DEFAULT_TITLE)
            .ingress(DEFAULT_INGRESS)
            .body(DEFAULT_BODY)
            .caption(DEFAULT_CAPTION)
            .visibility(DEFAULT_VISIBILITY);
        // Add required entity
        FragmentEntity fragment;
        if (TestUtil.findAll(em, Fragment.class).isEmpty()) {
            fragment = FragmentResourceIT.createEntity(em);
            em.persist(fragment);
            em.flush();
        } else {
            fragment = TestUtil.findAll(em, Fragment.class).get(0);
        }
        localizedEntity.setFragment(fragment);
        return localizedEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocalizedEntity createUpdatedEntity(EntityManager em) {
        LocalizedEntity localizedEntity = new LocalizedEntity()
            .i18n(UPDATED_I_18_N)
            .title(UPDATED_TITLE)
            .ingress(UPDATED_INGRESS)
            .body(UPDATED_BODY)
            .caption(UPDATED_CAPTION)
            .visibility(UPDATED_VISIBILITY);
        // Add required entity
        FragmentEntity fragment;
        if (TestUtil.findAll(em, Fragment.class).isEmpty()) {
            fragment = FragmentResourceIT.createUpdatedEntity(em);
            em.persist(fragment);
            em.flush();
        } else {
            fragment = TestUtil.findAll(em, Fragment.class).get(0);
        }
        localizedEntity.setFragment(fragment);
        return localizedEntity;
    }

    @BeforeEach
    public void initTest() {
        localizedEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createLocalized() throws Exception {
        int databaseSizeBeforeCreate = localizedRepository.findAll().size();
        // Create the Localized
        restLocalizedMockMvc.perform(post("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(localizedEntity)))
            .andExpect(status().isCreated());

        // Validate the Localized in the database
        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeCreate + 1);
        LocalizedEntity testLocalized = localizedList.get(localizedList.size() - 1);
        assertThat(testLocalized.geti18n()).isEqualTo(DEFAULT_I_18_N);
        assertThat(testLocalized.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLocalized.getIngress()).isEqualTo(DEFAULT_INGRESS);
        assertThat(testLocalized.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testLocalized.getCaption()).isEqualTo(DEFAULT_CAPTION);
        assertThat(testLocalized.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);

        // Validate the Localized in Elasticsearch
        verify(mockLocalizedSearchRepository, times(1)).save(testLocalized);
    }

    @Test
    @Transactional
    public void createLocalizedWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = localizedRepository.findAll().size();

        // Create the Localized with an existing ID
        localizedEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocalizedMockMvc.perform(post("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(localizedEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Localized in the database
        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeCreate);

        // Validate the Localized in Elasticsearch
        verify(mockLocalizedSearchRepository, times(0)).save(localizedEntity);
    }


    @Test
    @Transactional
    public void checki18nIsRequired() throws Exception {
        int databaseSizeBeforeTest = localizedRepository.findAll().size();
        // set the field null
        localizedEntity.seti18n(null);

        // Create the Localized, which fails.


        restLocalizedMockMvc.perform(post("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(localizedEntity)))
            .andExpect(status().isBadRequest());

        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = localizedRepository.findAll().size();
        // set the field null
        localizedEntity.setTitle(null);

        // Create the Localized, which fails.


        restLocalizedMockMvc.perform(post("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(localizedEntity)))
            .andExpect(status().isBadRequest());

        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLocalizeds() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList
        restLocalizedMockMvc.perform(get("/api/localizeds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(localizedEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].i18n").value(hasItem(DEFAULT_I_18_N)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
    
    @Test
    @Transactional
    public void getLocalized() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get the localized
        restLocalizedMockMvc.perform(get("/api/localizeds/{id}", localizedEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(localizedEntity.getId().intValue()))
            .andExpect(jsonPath("$.i18n").value(DEFAULT_I_18_N))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.ingress").value(DEFAULT_INGRESS))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()));
    }


    @Test
    @Transactional
    public void getLocalizedsByIdFiltering() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        Long id = localizedEntity.getId();

        defaultLocalizedShouldBeFound("id.equals=" + id);
        defaultLocalizedShouldNotBeFound("id.notEquals=" + id);

        defaultLocalizedShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLocalizedShouldNotBeFound("id.greaterThan=" + id);

        defaultLocalizedShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLocalizedShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllLocalizedsByi18nIsEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n equals to DEFAULT_I_18_N
        defaultLocalizedShouldBeFound("i18n.equals=" + DEFAULT_I_18_N);

        // Get all the localizedList where i18n equals to UPDATED_I_18_N
        defaultLocalizedShouldNotBeFound("i18n.equals=" + UPDATED_I_18_N);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByi18nIsNotEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n not equals to DEFAULT_I_18_N
        defaultLocalizedShouldNotBeFound("i18n.notEquals=" + DEFAULT_I_18_N);

        // Get all the localizedList where i18n not equals to UPDATED_I_18_N
        defaultLocalizedShouldBeFound("i18n.notEquals=" + UPDATED_I_18_N);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByi18nIsInShouldWork() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n in DEFAULT_I_18_N or UPDATED_I_18_N
        defaultLocalizedShouldBeFound("i18n.in=" + DEFAULT_I_18_N + "," + UPDATED_I_18_N);

        // Get all the localizedList where i18n equals to UPDATED_I_18_N
        defaultLocalizedShouldNotBeFound("i18n.in=" + UPDATED_I_18_N);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByi18nIsNullOrNotNull() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n is not null
        defaultLocalizedShouldBeFound("i18n.specified=true");

        // Get all the localizedList where i18n is null
        defaultLocalizedShouldNotBeFound("i18n.specified=false");
    }
                @Test
    @Transactional
    public void getAllLocalizedsByi18nContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n contains DEFAULT_I_18_N
        defaultLocalizedShouldBeFound("i18n.contains=" + DEFAULT_I_18_N);

        // Get all the localizedList where i18n contains UPDATED_I_18_N
        defaultLocalizedShouldNotBeFound("i18n.contains=" + UPDATED_I_18_N);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByi18nNotContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where i18n does not contain DEFAULT_I_18_N
        defaultLocalizedShouldNotBeFound("i18n.doesNotContain=" + DEFAULT_I_18_N);

        // Get all the localizedList where i18n does not contain UPDATED_I_18_N
        defaultLocalizedShouldBeFound("i18n.doesNotContain=" + UPDATED_I_18_N);
    }


    @Test
    @Transactional
    public void getAllLocalizedsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title equals to DEFAULT_TITLE
        defaultLocalizedShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the localizedList where title equals to UPDATED_TITLE
        defaultLocalizedShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title not equals to DEFAULT_TITLE
        defaultLocalizedShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the localizedList where title not equals to UPDATED_TITLE
        defaultLocalizedShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultLocalizedShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the localizedList where title equals to UPDATED_TITLE
        defaultLocalizedShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title is not null
        defaultLocalizedShouldBeFound("title.specified=true");

        // Get all the localizedList where title is null
        defaultLocalizedShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllLocalizedsByTitleContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title contains DEFAULT_TITLE
        defaultLocalizedShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the localizedList where title contains UPDATED_TITLE
        defaultLocalizedShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where title does not contain DEFAULT_TITLE
        defaultLocalizedShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the localizedList where title does not contain UPDATED_TITLE
        defaultLocalizedShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllLocalizedsByIngressIsEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress equals to DEFAULT_INGRESS
        defaultLocalizedShouldBeFound("ingress.equals=" + DEFAULT_INGRESS);

        // Get all the localizedList where ingress equals to UPDATED_INGRESS
        defaultLocalizedShouldNotBeFound("ingress.equals=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByIngressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress not equals to DEFAULT_INGRESS
        defaultLocalizedShouldNotBeFound("ingress.notEquals=" + DEFAULT_INGRESS);

        // Get all the localizedList where ingress not equals to UPDATED_INGRESS
        defaultLocalizedShouldBeFound("ingress.notEquals=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByIngressIsInShouldWork() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress in DEFAULT_INGRESS or UPDATED_INGRESS
        defaultLocalizedShouldBeFound("ingress.in=" + DEFAULT_INGRESS + "," + UPDATED_INGRESS);

        // Get all the localizedList where ingress equals to UPDATED_INGRESS
        defaultLocalizedShouldNotBeFound("ingress.in=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByIngressIsNullOrNotNull() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress is not null
        defaultLocalizedShouldBeFound("ingress.specified=true");

        // Get all the localizedList where ingress is null
        defaultLocalizedShouldNotBeFound("ingress.specified=false");
    }
                @Test
    @Transactional
    public void getAllLocalizedsByIngressContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress contains DEFAULT_INGRESS
        defaultLocalizedShouldBeFound("ingress.contains=" + DEFAULT_INGRESS);

        // Get all the localizedList where ingress contains UPDATED_INGRESS
        defaultLocalizedShouldNotBeFound("ingress.contains=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByIngressNotContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where ingress does not contain DEFAULT_INGRESS
        defaultLocalizedShouldNotBeFound("ingress.doesNotContain=" + DEFAULT_INGRESS);

        // Get all the localizedList where ingress does not contain UPDATED_INGRESS
        defaultLocalizedShouldBeFound("ingress.doesNotContain=" + UPDATED_INGRESS);
    }


    @Test
    @Transactional
    public void getAllLocalizedsByCaptionIsEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption equals to DEFAULT_CAPTION
        defaultLocalizedShouldBeFound("caption.equals=" + DEFAULT_CAPTION);

        // Get all the localizedList where caption equals to UPDATED_CAPTION
        defaultLocalizedShouldNotBeFound("caption.equals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByCaptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption not equals to DEFAULT_CAPTION
        defaultLocalizedShouldNotBeFound("caption.notEquals=" + DEFAULT_CAPTION);

        // Get all the localizedList where caption not equals to UPDATED_CAPTION
        defaultLocalizedShouldBeFound("caption.notEquals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByCaptionIsInShouldWork() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption in DEFAULT_CAPTION or UPDATED_CAPTION
        defaultLocalizedShouldBeFound("caption.in=" + DEFAULT_CAPTION + "," + UPDATED_CAPTION);

        // Get all the localizedList where caption equals to UPDATED_CAPTION
        defaultLocalizedShouldNotBeFound("caption.in=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByCaptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption is not null
        defaultLocalizedShouldBeFound("caption.specified=true");

        // Get all the localizedList where caption is null
        defaultLocalizedShouldNotBeFound("caption.specified=false");
    }
                @Test
    @Transactional
    public void getAllLocalizedsByCaptionContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption contains DEFAULT_CAPTION
        defaultLocalizedShouldBeFound("caption.contains=" + DEFAULT_CAPTION);

        // Get all the localizedList where caption contains UPDATED_CAPTION
        defaultLocalizedShouldNotBeFound("caption.contains=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByCaptionNotContainsSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where caption does not contain DEFAULT_CAPTION
        defaultLocalizedShouldNotBeFound("caption.doesNotContain=" + DEFAULT_CAPTION);

        // Get all the localizedList where caption does not contain UPDATED_CAPTION
        defaultLocalizedShouldBeFound("caption.doesNotContain=" + UPDATED_CAPTION);
    }


    @Test
    @Transactional
    public void getAllLocalizedsByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where visibility equals to DEFAULT_VISIBILITY
        defaultLocalizedShouldBeFound("visibility.equals=" + DEFAULT_VISIBILITY);

        // Get all the localizedList where visibility equals to UPDATED_VISIBILITY
        defaultLocalizedShouldNotBeFound("visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByVisibilityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where visibility not equals to DEFAULT_VISIBILITY
        defaultLocalizedShouldNotBeFound("visibility.notEquals=" + DEFAULT_VISIBILITY);

        // Get all the localizedList where visibility not equals to UPDATED_VISIBILITY
        defaultLocalizedShouldBeFound("visibility.notEquals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where visibility in DEFAULT_VISIBILITY or UPDATED_VISIBILITY
        defaultLocalizedShouldBeFound("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY);

        // Get all the localizedList where visibility equals to UPDATED_VISIBILITY
        defaultLocalizedShouldNotBeFound("visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllLocalizedsByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        localizedRepository.saveAndFlush(localizedEntity);

        // Get all the localizedList where visibility is not null
        defaultLocalizedShouldBeFound("visibility.specified=true");

        // Get all the localizedList where visibility is null
        defaultLocalizedShouldNotBeFound("visibility.specified=false");
    }

    @Test
    @Transactional
    public void getAllLocalizedsByFragmentIsEqualToSomething() throws Exception {
        // Get already existing entity
        FragmentEntity fragment = localizedEntity.getFragment();
        localizedRepository.saveAndFlush(localizedEntity);
        Long fragmentId = fragment.getId();

        // Get all the localizedList where fragment equals to fragmentId
        defaultLocalizedShouldBeFound("fragmentId.equals=" + fragmentId);

        // Get all the localizedList where fragment equals to fragmentId + 1
        defaultLocalizedShouldNotBeFound("fragmentId.equals=" + (fragmentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLocalizedShouldBeFound(String filter) throws Exception {
        restLocalizedMockMvc.perform(get("/api/localizeds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(localizedEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].i18n").value(hasItem(DEFAULT_I_18_N)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));

        // Check, that the count call also returns 1
        restLocalizedMockMvc.perform(get("/api/localizeds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLocalizedShouldNotBeFound(String filter) throws Exception {
        restLocalizedMockMvc.perform(get("/api/localizeds?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLocalizedMockMvc.perform(get("/api/localizeds/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingLocalized() throws Exception {
        // Get the localized
        restLocalizedMockMvc.perform(get("/api/localizeds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocalized() throws Exception {
        // Initialize the database
        localizedService.save(localizedEntity);

        int databaseSizeBeforeUpdate = localizedRepository.findAll().size();

        // Update the localized
        LocalizedEntity updatedLocalizedEntity = localizedRepository.findById(localizedEntity.getId()).get();
        // Disconnect from session so that the updates on updatedLocalizedEntity are not directly saved in db
        em.detach(updatedLocalizedEntity);
        updatedLocalizedEntity
            .i18n(UPDATED_I_18_N)
            .title(UPDATED_TITLE)
            .ingress(UPDATED_INGRESS)
            .body(UPDATED_BODY)
            .caption(UPDATED_CAPTION)
            .visibility(UPDATED_VISIBILITY);

        restLocalizedMockMvc.perform(put("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLocalizedEntity)))
            .andExpect(status().isOk());

        // Validate the Localized in the database
        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeUpdate);
        LocalizedEntity testLocalized = localizedList.get(localizedList.size() - 1);
        assertThat(testLocalized.geti18n()).isEqualTo(UPDATED_I_18_N);
        assertThat(testLocalized.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLocalized.getIngress()).isEqualTo(UPDATED_INGRESS);
        assertThat(testLocalized.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testLocalized.getCaption()).isEqualTo(UPDATED_CAPTION);
        assertThat(testLocalized.getVisibility()).isEqualTo(UPDATED_VISIBILITY);

        // Validate the Localized in Elasticsearch
        verify(mockLocalizedSearchRepository, times(2)).save(testLocalized);
    }

    @Test
    @Transactional
    public void updateNonExistingLocalized() throws Exception {
        int databaseSizeBeforeUpdate = localizedRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocalizedMockMvc.perform(put("/api/localizeds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(localizedEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Localized in the database
        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localized in Elasticsearch
        verify(mockLocalizedSearchRepository, times(0)).save(localizedEntity);
    }

    @Test
    @Transactional
    public void deleteLocalized() throws Exception {
        // Initialize the database
        localizedService.save(localizedEntity);

        int databaseSizeBeforeDelete = localizedRepository.findAll().size();

        // Delete the localized
        restLocalizedMockMvc.perform(delete("/api/localizeds/{id}", localizedEntity.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LocalizedEntity> localizedList = localizedRepository.findAll();
        assertThat(localizedList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Localized in Elasticsearch
        verify(mockLocalizedSearchRepository, times(1)).deleteById(localizedEntity.getId());
    }

    @Test
    @Transactional
    public void searchLocalized() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        localizedService.save(localizedEntity);
        when(mockLocalizedSearchRepository.search(queryStringQuery("id:" + localizedEntity.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(localizedEntity), PageRequest.of(0, 1), 1));

        // Search the localized
        restLocalizedMockMvc.perform(get("/api/_search/localizeds?query=id:" + localizedEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(localizedEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].i18n").value(hasItem(DEFAULT_I_18_N)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
}
