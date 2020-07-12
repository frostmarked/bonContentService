package com.bonlimousin.content.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import com.bonlimousin.content.BonContentServiceApp;
import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.domain.LocalizedEntity;
import com.bonlimousin.content.domain.StoryEntity;
import com.bonlimousin.content.domain.TagEntity;
import com.bonlimousin.content.domain.enumeration.FragmentTemplate;
import com.bonlimousin.content.domain.enumeration.UserRole;
import com.bonlimousin.content.repository.FragmentRepository;
import com.bonlimousin.content.repository.search.FragmentSearchRepository;
import com.bonlimousin.content.service.FragmentQueryService;
import com.bonlimousin.content.service.FragmentService;
/**
 * Integration tests for the {@link FragmentResource} REST controller.
 */
@SpringBootTest(classes = BonContentServiceApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class FragmentResourceIT {

    private static final FragmentTemplate DEFAULT_TEMPLATE = FragmentTemplate.V1;
    private static final FragmentTemplate UPDATED_TEMPLATE = FragmentTemplate.V2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_INGRESS = "AAAAAAAAAA";
    private static final String UPDATED_INGRESS = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_WIDTH = 1;
    private static final Integer UPDATED_WIDTH = 2;
    private static final Integer SMALLER_WIDTH = 1 - 1;

    private static final Integer DEFAULT_HEIGHT = 1;
    private static final Integer UPDATED_HEIGHT = 2;
    private static final Integer SMALLER_HEIGHT = 1 - 1;

    private static final Integer DEFAULT_ORDER_NO = 1;
    private static final Integer UPDATED_ORDER_NO = 2;
    private static final Integer SMALLER_ORDER_NO = 1 - 1;

    private static final UserRole DEFAULT_VISIBILITY = UserRole.ROLE_ADMIN;
    private static final UserRole UPDATED_VISIBILITY = UserRole.ROLE_USER;

    @Autowired
    private FragmentRepository fragmentRepository;

    @Mock
    private FragmentRepository fragmentRepositoryMock;

    @Mock
    private FragmentService fragmentServiceMock;

    @Autowired
    private FragmentService fragmentService;

    /**
     * This repository is mocked in the com.bonlimousin.content.repository.search test package.
     *
     * @see com.bonlimousin.content.repository.search.FragmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private FragmentSearchRepository mockFragmentSearchRepository;

    @Autowired
    private FragmentQueryService fragmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFragmentMockMvc;

    private FragmentEntity fragmentEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FragmentEntity createEntity(EntityManager em) {
        FragmentEntity fragmentEntity = new FragmentEntity()
            .template(DEFAULT_TEMPLATE)
            .name(DEFAULT_NAME)
            .title(DEFAULT_TITLE)
            .ingress(DEFAULT_INGRESS)
            .body(DEFAULT_BODY)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .caption(DEFAULT_CAPTION)
            .width(DEFAULT_WIDTH)
            .height(DEFAULT_HEIGHT)
            .orderNo(DEFAULT_ORDER_NO)
            .visibility(DEFAULT_VISIBILITY);
        // Add required entity
        StoryEntity story;
        if (TestUtil.findAll(em, StoryEntity.class).isEmpty()) {
            story = StoryResourceIT.createEntity(em);
            em.persist(story);
            em.flush();
        } else {
            story = TestUtil.findAll(em, StoryEntity.class).get(0);
        }
        fragmentEntity.setStory(story);
        return fragmentEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FragmentEntity createUpdatedEntity(EntityManager em) {
        FragmentEntity fragmentEntity = new FragmentEntity()
            .template(UPDATED_TEMPLATE)
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .ingress(UPDATED_INGRESS)
            .body(UPDATED_BODY)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .caption(UPDATED_CAPTION)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .orderNo(UPDATED_ORDER_NO)
            .visibility(UPDATED_VISIBILITY);
        // Add required entity
        StoryEntity story;
        if (TestUtil.findAll(em, StoryEntity.class).isEmpty()) {
            story = StoryResourceIT.createUpdatedEntity(em);
            em.persist(story);
            em.flush();
        } else {
            story = TestUtil.findAll(em, StoryEntity.class).get(0);
        }
        fragmentEntity.setStory(story);
        return fragmentEntity;
    }

    @BeforeEach
    public void initTest() {
        fragmentEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createFragment() throws Exception {
        int databaseSizeBeforeCreate = fragmentRepository.findAll().size();
        // Create the Fragment
        restFragmentMockMvc.perform(post("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isCreated());

        // Validate the Fragment in the database
        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeCreate + 1);
        FragmentEntity testFragment = fragmentList.get(fragmentList.size() - 1);
        assertThat(testFragment.getTemplate()).isEqualTo(DEFAULT_TEMPLATE);
        assertThat(testFragment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFragment.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFragment.getIngress()).isEqualTo(DEFAULT_INGRESS);
        assertThat(testFragment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testFragment.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testFragment.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testFragment.getCaption()).isEqualTo(DEFAULT_CAPTION);
        assertThat(testFragment.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testFragment.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testFragment.getOrderNo()).isEqualTo(DEFAULT_ORDER_NO);
        assertThat(testFragment.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);

        // Validate the Fragment in Elasticsearch
        verify(mockFragmentSearchRepository, times(1)).save(testFragment);
    }

    @Test
    @Transactional
    public void createFragmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fragmentRepository.findAll().size();

        // Create the Fragment with an existing ID
        fragmentEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFragmentMockMvc.perform(post("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Fragment in the database
        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Fragment in Elasticsearch
        verify(mockFragmentSearchRepository, times(0)).save(fragmentEntity);
    }


    @Test
    @Transactional
    public void checkTemplateIsRequired() throws Exception {
        int databaseSizeBeforeTest = fragmentRepository.findAll().size();
        // set the field null
        fragmentEntity.setTemplate(null);

        // Create the Fragment, which fails.


        restFragmentMockMvc.perform(post("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isBadRequest());

        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fragmentRepository.findAll().size();
        // set the field null
        fragmentEntity.setName(null);

        // Create the Fragment, which fails.


        restFragmentMockMvc.perform(post("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isBadRequest());

        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOrderNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = fragmentRepository.findAll().size();
        // set the field null
        fragmentEntity.setOrderNo(null);

        // Create the Fragment, which fails.


        restFragmentMockMvc.perform(post("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isBadRequest());

        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFragments() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList
        restFragmentMockMvc.perform(get("/api/fragments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fragmentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].template").value(hasItem(DEFAULT_TEMPLATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].orderNo").value(hasItem(DEFAULT_ORDER_NO)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllFragmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(fragmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFragmentMockMvc.perform(get("/api/fragments?eagerload=true"))
            .andExpect(status().isOk());

        verify(fragmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllFragmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(fragmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFragmentMockMvc.perform(get("/api/fragments?eagerload=true"))
            .andExpect(status().isOk());

        verify(fragmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getFragment() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get the fragment
        restFragmentMockMvc.perform(get("/api/fragments/{id}", fragmentEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fragmentEntity.getId().intValue()))
            .andExpect(jsonPath("$.template").value(DEFAULT_TEMPLATE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.ingress").value(DEFAULT_INGRESS))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT))
            .andExpect(jsonPath("$.orderNo").value(DEFAULT_ORDER_NO))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()));
    }


    @Test
    @Transactional
    public void getFragmentsByIdFiltering() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        Long id = fragmentEntity.getId();

        defaultFragmentShouldBeFound("id.equals=" + id);
        defaultFragmentShouldNotBeFound("id.notEquals=" + id);

        defaultFragmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFragmentShouldNotBeFound("id.greaterThan=" + id);

        defaultFragmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFragmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllFragmentsByTemplateIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where template equals to DEFAULT_TEMPLATE
        defaultFragmentShouldBeFound("template.equals=" + DEFAULT_TEMPLATE);

        // Get all the fragmentList where template equals to UPDATED_TEMPLATE
        defaultFragmentShouldNotBeFound("template.equals=" + UPDATED_TEMPLATE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTemplateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where template not equals to DEFAULT_TEMPLATE
        defaultFragmentShouldNotBeFound("template.notEquals=" + DEFAULT_TEMPLATE);

        // Get all the fragmentList where template not equals to UPDATED_TEMPLATE
        defaultFragmentShouldBeFound("template.notEquals=" + UPDATED_TEMPLATE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTemplateIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where template in DEFAULT_TEMPLATE or UPDATED_TEMPLATE
        defaultFragmentShouldBeFound("template.in=" + DEFAULT_TEMPLATE + "," + UPDATED_TEMPLATE);

        // Get all the fragmentList where template equals to UPDATED_TEMPLATE
        defaultFragmentShouldNotBeFound("template.in=" + UPDATED_TEMPLATE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTemplateIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where template is not null
        defaultFragmentShouldBeFound("template.specified=true");

        // Get all the fragmentList where template is null
        defaultFragmentShouldNotBeFound("template.specified=false");
    }

    @Test
    @Transactional
    public void getAllFragmentsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name equals to DEFAULT_NAME
        defaultFragmentShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the fragmentList where name equals to UPDATED_NAME
        defaultFragmentShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllFragmentsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name not equals to DEFAULT_NAME
        defaultFragmentShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the fragmentList where name not equals to UPDATED_NAME
        defaultFragmentShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllFragmentsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFragmentShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the fragmentList where name equals to UPDATED_NAME
        defaultFragmentShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllFragmentsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name is not null
        defaultFragmentShouldBeFound("name.specified=true");

        // Get all the fragmentList where name is null
        defaultFragmentShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllFragmentsByNameContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name contains DEFAULT_NAME
        defaultFragmentShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the fragmentList where name contains UPDATED_NAME
        defaultFragmentShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllFragmentsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where name does not contain DEFAULT_NAME
        defaultFragmentShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the fragmentList where name does not contain UPDATED_NAME
        defaultFragmentShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllFragmentsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title equals to DEFAULT_TITLE
        defaultFragmentShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the fragmentList where title equals to UPDATED_TITLE
        defaultFragmentShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title not equals to DEFAULT_TITLE
        defaultFragmentShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the fragmentList where title not equals to UPDATED_TITLE
        defaultFragmentShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultFragmentShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the fragmentList where title equals to UPDATED_TITLE
        defaultFragmentShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title is not null
        defaultFragmentShouldBeFound("title.specified=true");

        // Get all the fragmentList where title is null
        defaultFragmentShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllFragmentsByTitleContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title contains DEFAULT_TITLE
        defaultFragmentShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the fragmentList where title contains UPDATED_TITLE
        defaultFragmentShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllFragmentsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where title does not contain DEFAULT_TITLE
        defaultFragmentShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the fragmentList where title does not contain UPDATED_TITLE
        defaultFragmentShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllFragmentsByIngressIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress equals to DEFAULT_INGRESS
        defaultFragmentShouldBeFound("ingress.equals=" + DEFAULT_INGRESS);

        // Get all the fragmentList where ingress equals to UPDATED_INGRESS
        defaultFragmentShouldNotBeFound("ingress.equals=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllFragmentsByIngressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress not equals to DEFAULT_INGRESS
        defaultFragmentShouldNotBeFound("ingress.notEquals=" + DEFAULT_INGRESS);

        // Get all the fragmentList where ingress not equals to UPDATED_INGRESS
        defaultFragmentShouldBeFound("ingress.notEquals=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllFragmentsByIngressIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress in DEFAULT_INGRESS or UPDATED_INGRESS
        defaultFragmentShouldBeFound("ingress.in=" + DEFAULT_INGRESS + "," + UPDATED_INGRESS);

        // Get all the fragmentList where ingress equals to UPDATED_INGRESS
        defaultFragmentShouldNotBeFound("ingress.in=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllFragmentsByIngressIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress is not null
        defaultFragmentShouldBeFound("ingress.specified=true");

        // Get all the fragmentList where ingress is null
        defaultFragmentShouldNotBeFound("ingress.specified=false");
    }
                @Test
    @Transactional
    public void getAllFragmentsByIngressContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress contains DEFAULT_INGRESS
        defaultFragmentShouldBeFound("ingress.contains=" + DEFAULT_INGRESS);

        // Get all the fragmentList where ingress contains UPDATED_INGRESS
        defaultFragmentShouldNotBeFound("ingress.contains=" + UPDATED_INGRESS);
    }

    @Test
    @Transactional
    public void getAllFragmentsByIngressNotContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where ingress does not contain DEFAULT_INGRESS
        defaultFragmentShouldNotBeFound("ingress.doesNotContain=" + DEFAULT_INGRESS);

        // Get all the fragmentList where ingress does not contain UPDATED_INGRESS
        defaultFragmentShouldBeFound("ingress.doesNotContain=" + UPDATED_INGRESS);
    }


    @Test
    @Transactional
    public void getAllFragmentsByCaptionIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption equals to DEFAULT_CAPTION
        defaultFragmentShouldBeFound("caption.equals=" + DEFAULT_CAPTION);

        // Get all the fragmentList where caption equals to UPDATED_CAPTION
        defaultFragmentShouldNotBeFound("caption.equals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllFragmentsByCaptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption not equals to DEFAULT_CAPTION
        defaultFragmentShouldNotBeFound("caption.notEquals=" + DEFAULT_CAPTION);

        // Get all the fragmentList where caption not equals to UPDATED_CAPTION
        defaultFragmentShouldBeFound("caption.notEquals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllFragmentsByCaptionIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption in DEFAULT_CAPTION or UPDATED_CAPTION
        defaultFragmentShouldBeFound("caption.in=" + DEFAULT_CAPTION + "," + UPDATED_CAPTION);

        // Get all the fragmentList where caption equals to UPDATED_CAPTION
        defaultFragmentShouldNotBeFound("caption.in=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllFragmentsByCaptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption is not null
        defaultFragmentShouldBeFound("caption.specified=true");

        // Get all the fragmentList where caption is null
        defaultFragmentShouldNotBeFound("caption.specified=false");
    }
                @Test
    @Transactional
    public void getAllFragmentsByCaptionContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption contains DEFAULT_CAPTION
        defaultFragmentShouldBeFound("caption.contains=" + DEFAULT_CAPTION);

        // Get all the fragmentList where caption contains UPDATED_CAPTION
        defaultFragmentShouldNotBeFound("caption.contains=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllFragmentsByCaptionNotContainsSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where caption does not contain DEFAULT_CAPTION
        defaultFragmentShouldNotBeFound("caption.doesNotContain=" + DEFAULT_CAPTION);

        // Get all the fragmentList where caption does not contain UPDATED_CAPTION
        defaultFragmentShouldBeFound("caption.doesNotContain=" + UPDATED_CAPTION);
    }


    @Test
    @Transactional
    public void getAllFragmentsByWidthIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width equals to DEFAULT_WIDTH
        defaultFragmentShouldBeFound("width.equals=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width equals to UPDATED_WIDTH
        defaultFragmentShouldNotBeFound("width.equals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width not equals to DEFAULT_WIDTH
        defaultFragmentShouldNotBeFound("width.notEquals=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width not equals to UPDATED_WIDTH
        defaultFragmentShouldBeFound("width.notEquals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width in DEFAULT_WIDTH or UPDATED_WIDTH
        defaultFragmentShouldBeFound("width.in=" + DEFAULT_WIDTH + "," + UPDATED_WIDTH);

        // Get all the fragmentList where width equals to UPDATED_WIDTH
        defaultFragmentShouldNotBeFound("width.in=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width is not null
        defaultFragmentShouldBeFound("width.specified=true");

        // Get all the fragmentList where width is null
        defaultFragmentShouldNotBeFound("width.specified=false");
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width is greater than or equal to DEFAULT_WIDTH
        defaultFragmentShouldBeFound("width.greaterThanOrEqual=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width is greater than or equal to UPDATED_WIDTH
        defaultFragmentShouldNotBeFound("width.greaterThanOrEqual=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width is less than or equal to DEFAULT_WIDTH
        defaultFragmentShouldBeFound("width.lessThanOrEqual=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width is less than or equal to SMALLER_WIDTH
        defaultFragmentShouldNotBeFound("width.lessThanOrEqual=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsLessThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width is less than DEFAULT_WIDTH
        defaultFragmentShouldNotBeFound("width.lessThan=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width is less than UPDATED_WIDTH
        defaultFragmentShouldBeFound("width.lessThan=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    public void getAllFragmentsByWidthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where width is greater than DEFAULT_WIDTH
        defaultFragmentShouldNotBeFound("width.greaterThan=" + DEFAULT_WIDTH);

        // Get all the fragmentList where width is greater than SMALLER_WIDTH
        defaultFragmentShouldBeFound("width.greaterThan=" + SMALLER_WIDTH);
    }


    @Test
    @Transactional
    public void getAllFragmentsByHeightIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height equals to DEFAULT_HEIGHT
        defaultFragmentShouldBeFound("height.equals=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height equals to UPDATED_HEIGHT
        defaultFragmentShouldNotBeFound("height.equals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height not equals to DEFAULT_HEIGHT
        defaultFragmentShouldNotBeFound("height.notEquals=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height not equals to UPDATED_HEIGHT
        defaultFragmentShouldBeFound("height.notEquals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height in DEFAULT_HEIGHT or UPDATED_HEIGHT
        defaultFragmentShouldBeFound("height.in=" + DEFAULT_HEIGHT + "," + UPDATED_HEIGHT);

        // Get all the fragmentList where height equals to UPDATED_HEIGHT
        defaultFragmentShouldNotBeFound("height.in=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height is not null
        defaultFragmentShouldBeFound("height.specified=true");

        // Get all the fragmentList where height is null
        defaultFragmentShouldNotBeFound("height.specified=false");
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height is greater than or equal to DEFAULT_HEIGHT
        defaultFragmentShouldBeFound("height.greaterThanOrEqual=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height is greater than or equal to UPDATED_HEIGHT
        defaultFragmentShouldNotBeFound("height.greaterThanOrEqual=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height is less than or equal to DEFAULT_HEIGHT
        defaultFragmentShouldBeFound("height.lessThanOrEqual=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height is less than or equal to SMALLER_HEIGHT
        defaultFragmentShouldNotBeFound("height.lessThanOrEqual=" + SMALLER_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsLessThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height is less than DEFAULT_HEIGHT
        defaultFragmentShouldNotBeFound("height.lessThan=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height is less than UPDATED_HEIGHT
        defaultFragmentShouldBeFound("height.lessThan=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void getAllFragmentsByHeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where height is greater than DEFAULT_HEIGHT
        defaultFragmentShouldNotBeFound("height.greaterThan=" + DEFAULT_HEIGHT);

        // Get all the fragmentList where height is greater than SMALLER_HEIGHT
        defaultFragmentShouldBeFound("height.greaterThan=" + SMALLER_HEIGHT);
    }


    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo equals to DEFAULT_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.equals=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo equals to UPDATED_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.equals=" + UPDATED_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo not equals to DEFAULT_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.notEquals=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo not equals to UPDATED_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.notEquals=" + UPDATED_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo in DEFAULT_ORDER_NO or UPDATED_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.in=" + DEFAULT_ORDER_NO + "," + UPDATED_ORDER_NO);

        // Get all the fragmentList where orderNo equals to UPDATED_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.in=" + UPDATED_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo is not null
        defaultFragmentShouldBeFound("orderNo.specified=true");

        // Get all the fragmentList where orderNo is null
        defaultFragmentShouldNotBeFound("orderNo.specified=false");
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo is greater than or equal to DEFAULT_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.greaterThanOrEqual=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo is greater than or equal to UPDATED_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.greaterThanOrEqual=" + UPDATED_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo is less than or equal to DEFAULT_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.lessThanOrEqual=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo is less than or equal to SMALLER_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.lessThanOrEqual=" + SMALLER_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsLessThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo is less than DEFAULT_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.lessThan=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo is less than UPDATED_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.lessThan=" + UPDATED_ORDER_NO);
    }

    @Test
    @Transactional
    public void getAllFragmentsByOrderNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where orderNo is greater than DEFAULT_ORDER_NO
        defaultFragmentShouldNotBeFound("orderNo.greaterThan=" + DEFAULT_ORDER_NO);

        // Get all the fragmentList where orderNo is greater than SMALLER_ORDER_NO
        defaultFragmentShouldBeFound("orderNo.greaterThan=" + SMALLER_ORDER_NO);
    }


    @Test
    @Transactional
    public void getAllFragmentsByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where visibility equals to DEFAULT_VISIBILITY
        defaultFragmentShouldBeFound("visibility.equals=" + DEFAULT_VISIBILITY);

        // Get all the fragmentList where visibility equals to UPDATED_VISIBILITY
        defaultFragmentShouldNotBeFound("visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllFragmentsByVisibilityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where visibility not equals to DEFAULT_VISIBILITY
        defaultFragmentShouldNotBeFound("visibility.notEquals=" + DEFAULT_VISIBILITY);

        // Get all the fragmentList where visibility not equals to UPDATED_VISIBILITY
        defaultFragmentShouldBeFound("visibility.notEquals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllFragmentsByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where visibility in DEFAULT_VISIBILITY or UPDATED_VISIBILITY
        defaultFragmentShouldBeFound("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY);

        // Get all the fragmentList where visibility equals to UPDATED_VISIBILITY
        defaultFragmentShouldNotBeFound("visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllFragmentsByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);

        // Get all the fragmentList where visibility is not null
        defaultFragmentShouldBeFound("visibility.specified=true");

        // Get all the fragmentList where visibility is null
        defaultFragmentShouldNotBeFound("visibility.specified=false");
    }

    @Test
    @Transactional
    public void getAllFragmentsByLocalizedFragmentIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);
        LocalizedEntity localizedFragment = LocalizedResourceIT.createEntity(em);
        em.persist(localizedFragment);
        em.flush();
        fragmentEntity.addLocalizedFragment(localizedFragment);
        fragmentRepository.saveAndFlush(fragmentEntity);
        Long localizedFragmentId = localizedFragment.getId();

        // Get all the fragmentList where localizedFragment equals to localizedFragmentId
        defaultFragmentShouldBeFound("localizedFragmentId.equals=" + localizedFragmentId);

        // Get all the fragmentList where localizedFragment equals to localizedFragmentId + 1
        defaultFragmentShouldNotBeFound("localizedFragmentId.equals=" + (localizedFragmentId + 1));
    }


    @Test
    @Transactional
    public void getAllFragmentsByTagIsEqualToSomething() throws Exception {
        // Initialize the database
        fragmentRepository.saveAndFlush(fragmentEntity);
        TagEntity tag = com.bonlimousin.content.web.rest.TagResourceIT.createEntity(em);
        em.persist(tag);
        em.flush();
        fragmentEntity.addTag(tag);
        fragmentRepository.saveAndFlush(fragmentEntity);
        Long tagId = tag.getId();

        // Get all the fragmentList where tag equals to tagId
        defaultFragmentShouldBeFound("tagId.equals=" + tagId);

        // Get all the fragmentList where tag equals to tagId + 1
        defaultFragmentShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }


    @Test
    @Transactional
    public void getAllFragmentsByStoryIsEqualToSomething() throws Exception {
        // Get already existing entity
        StoryEntity story = fragmentEntity.getStory();
        fragmentRepository.saveAndFlush(fragmentEntity);
        Long storyId = story.getId();

        // Get all the fragmentList where story equals to storyId
        defaultFragmentShouldBeFound("storyId.equals=" + storyId);

        // Get all the fragmentList where story equals to storyId + 1
        defaultFragmentShouldNotBeFound("storyId.equals=" + (storyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFragmentShouldBeFound(String filter) throws Exception {
        restFragmentMockMvc.perform(get("/api/fragments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fragmentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].template").value(hasItem(DEFAULT_TEMPLATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].orderNo").value(hasItem(DEFAULT_ORDER_NO)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));

        // Check, that the count call also returns 1
        restFragmentMockMvc.perform(get("/api/fragments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFragmentShouldNotBeFound(String filter) throws Exception {
        restFragmentMockMvc.perform(get("/api/fragments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFragmentMockMvc.perform(get("/api/fragments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingFragment() throws Exception {
        // Get the fragment
        restFragmentMockMvc.perform(get("/api/fragments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFragment() throws Exception {
        // Initialize the database
        fragmentService.save(fragmentEntity);

        int databaseSizeBeforeUpdate = fragmentRepository.findAll().size();

        // Update the fragment
        FragmentEntity updatedFragmentEntity = fragmentRepository.findById(fragmentEntity.getId()).get();
        // Disconnect from session so that the updates on updatedFragmentEntity are not directly saved in db
        em.detach(updatedFragmentEntity);
        updatedFragmentEntity
            .template(UPDATED_TEMPLATE)
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .ingress(UPDATED_INGRESS)
            .body(UPDATED_BODY)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .caption(UPDATED_CAPTION)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .orderNo(UPDATED_ORDER_NO)
            .visibility(UPDATED_VISIBILITY);

        restFragmentMockMvc.perform(put("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedFragmentEntity)))
            .andExpect(status().isOk());

        // Validate the Fragment in the database
        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeUpdate);
        FragmentEntity testFragment = fragmentList.get(fragmentList.size() - 1);
        assertThat(testFragment.getTemplate()).isEqualTo(UPDATED_TEMPLATE);
        assertThat(testFragment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFragment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFragment.getIngress()).isEqualTo(UPDATED_INGRESS);
        assertThat(testFragment.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testFragment.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testFragment.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testFragment.getCaption()).isEqualTo(UPDATED_CAPTION);
        assertThat(testFragment.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testFragment.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testFragment.getOrderNo()).isEqualTo(UPDATED_ORDER_NO);
        assertThat(testFragment.getVisibility()).isEqualTo(UPDATED_VISIBILITY);

        // Validate the Fragment in Elasticsearch
        verify(mockFragmentSearchRepository, times(2)).save(testFragment);
    }

    @Test
    @Transactional
    public void updateNonExistingFragment() throws Exception {
        int databaseSizeBeforeUpdate = fragmentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFragmentMockMvc.perform(put("/api/fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(fragmentEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Fragment in the database
        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Fragment in Elasticsearch
        verify(mockFragmentSearchRepository, times(0)).save(fragmentEntity);
    }

    @Test
    @Transactional
    public void deleteFragment() throws Exception {
        // Initialize the database
        fragmentService.save(fragmentEntity);

        int databaseSizeBeforeDelete = fragmentRepository.findAll().size();

        // Delete the fragment
        restFragmentMockMvc.perform(delete("/api/fragments/{id}", fragmentEntity.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FragmentEntity> fragmentList = fragmentRepository.findAll();
        assertThat(fragmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Fragment in Elasticsearch
        verify(mockFragmentSearchRepository, times(1)).deleteById(fragmentEntity.getId());
    }

    @Test
    @Transactional
    public void searchFragment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        fragmentService.save(fragmentEntity);
        when(mockFragmentSearchRepository.search(queryStringQuery("id:" + fragmentEntity.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(fragmentEntity), PageRequest.of(0, 1), 1));

        // Search the fragment
        restFragmentMockMvc.perform(get("/api/_search/fragments?query=id:" + fragmentEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fragmentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].template").value(hasItem(DEFAULT_TEMPLATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ingress").value(hasItem(DEFAULT_INGRESS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].orderNo").value(hasItem(DEFAULT_ORDER_NO)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
}
