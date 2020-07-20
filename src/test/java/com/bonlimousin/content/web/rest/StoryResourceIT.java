package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.BonContentServiceApp;
import com.bonlimousin.content.domain.StoryEntity;
import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.repository.StoryRepository;
import com.bonlimousin.content.repository.search.StorySearchRepository;
import com.bonlimousin.content.service.StoryService;
import com.bonlimousin.content.service.dto.StoryCriteria;
import com.bonlimousin.content.service.StoryQueryService;

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
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bonlimousin.content.domain.enumeration.StoryCategory;
import com.bonlimousin.content.domain.enumeration.UserRole;
/**
 * Integration tests for the {@link StoryResource} REST controller.
 */
@SpringBootTest(classes = BonContentServiceApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class StoryResourceIT {

    private static final StoryCategory DEFAULT_CATEGORY = StoryCategory.NEWS;
    private static final StoryCategory UPDATED_CATEGORY = StoryCategory.MATRILINEALITY;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final UserRole DEFAULT_VISIBILITY = UserRole.ROLE_ADMIN;
    private static final UserRole UPDATED_VISIBILITY = UserRole.ROLE_USER;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryService storyService;

    /**
     * This repository is mocked in the com.bonlimousin.content.repository.search test package.
     *
     * @see com.bonlimousin.content.repository.search.StorySearchRepositoryMockConfiguration
     */
    @Autowired
    private StorySearchRepository mockStorySearchRepository;

    @Autowired
    private StoryQueryService storyQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoryMockMvc;

    private StoryEntity storyEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryEntity createEntity(EntityManager em) {
        StoryEntity storyEntity = new StoryEntity()
            .category(DEFAULT_CATEGORY)
            .name(DEFAULT_NAME)
            .visibility(DEFAULT_VISIBILITY);
        return storyEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StoryEntity createUpdatedEntity(EntityManager em) {
        StoryEntity storyEntity = new StoryEntity()
            .category(UPDATED_CATEGORY)
            .name(UPDATED_NAME)
            .visibility(UPDATED_VISIBILITY);
        return storyEntity;
    }

    @BeforeEach
    public void initTest() {
        storyEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createStory() throws Exception {
        int databaseSizeBeforeCreate = storyRepository.findAll().size();
        // Create the Story
        restStoryMockMvc.perform(post("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(storyEntity)))
            .andExpect(status().isCreated());

        // Validate the Story in the database
        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeCreate + 1);
        StoryEntity testStory = storyList.get(storyList.size() - 1);
        assertThat(testStory.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testStory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStory.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);

        // Validate the Story in Elasticsearch
        verify(mockStorySearchRepository, times(1)).save(testStory);
    }

    @Test
    @Transactional
    public void createStoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = storyRepository.findAll().size();

        // Create the Story with an existing ID
        storyEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoryMockMvc.perform(post("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(storyEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Story in Elasticsearch
        verify(mockStorySearchRepository, times(0)).save(storyEntity);
    }


    @Test
    @Transactional
    public void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        storyEntity.setCategory(null);

        // Create the Story, which fails.


        restStoryMockMvc.perform(post("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(storyEntity)))
            .andExpect(status().isBadRequest());

        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        storyEntity.setName(null);

        // Create the Story, which fails.


        restStoryMockMvc.perform(post("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(storyEntity)))
            .andExpect(status().isBadRequest());

        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStories() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList
        restStoryMockMvc.perform(get("/api/stories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
    
    @Test
    @Transactional
    public void getStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", storyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(storyEntity.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY.toString()));
    }


    @Test
    @Transactional
    public void getStoriesByIdFiltering() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        Long id = storyEntity.getId();

        defaultStoryShouldBeFound("id.equals=" + id);
        defaultStoryShouldNotBeFound("id.notEquals=" + id);

        defaultStoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStoryShouldNotBeFound("id.greaterThan=" + id);

        defaultStoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStoryShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllStoriesByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where category equals to DEFAULT_CATEGORY
        defaultStoryShouldBeFound("category.equals=" + DEFAULT_CATEGORY);

        // Get all the storyList where category equals to UPDATED_CATEGORY
        defaultStoryShouldNotBeFound("category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllStoriesByCategoryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where category not equals to DEFAULT_CATEGORY
        defaultStoryShouldNotBeFound("category.notEquals=" + DEFAULT_CATEGORY);

        // Get all the storyList where category not equals to UPDATED_CATEGORY
        defaultStoryShouldBeFound("category.notEquals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllStoriesByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where category in DEFAULT_CATEGORY or UPDATED_CATEGORY
        defaultStoryShouldBeFound("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY);

        // Get all the storyList where category equals to UPDATED_CATEGORY
        defaultStoryShouldNotBeFound("category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllStoriesByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where category is not null
        defaultStoryShouldBeFound("category.specified=true");

        // Get all the storyList where category is null
        defaultStoryShouldNotBeFound("category.specified=false");
    }

    @Test
    @Transactional
    public void getAllStoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name equals to DEFAULT_NAME
        defaultStoryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the storyList where name equals to UPDATED_NAME
        defaultStoryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStoriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name not equals to DEFAULT_NAME
        defaultStoryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the storyList where name not equals to UPDATED_NAME
        defaultStoryShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStoryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the storyList where name equals to UPDATED_NAME
        defaultStoryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name is not null
        defaultStoryShouldBeFound("name.specified=true");

        // Get all the storyList where name is null
        defaultStoryShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllStoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name contains DEFAULT_NAME
        defaultStoryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the storyList where name contains UPDATED_NAME
        defaultStoryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where name does not contain DEFAULT_NAME
        defaultStoryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the storyList where name does not contain UPDATED_NAME
        defaultStoryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllStoriesByVisibilityIsEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where visibility equals to DEFAULT_VISIBILITY
        defaultStoryShouldBeFound("visibility.equals=" + DEFAULT_VISIBILITY);

        // Get all the storyList where visibility equals to UPDATED_VISIBILITY
        defaultStoryShouldNotBeFound("visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllStoriesByVisibilityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where visibility not equals to DEFAULT_VISIBILITY
        defaultStoryShouldNotBeFound("visibility.notEquals=" + DEFAULT_VISIBILITY);

        // Get all the storyList where visibility not equals to UPDATED_VISIBILITY
        defaultStoryShouldBeFound("visibility.notEquals=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllStoriesByVisibilityIsInShouldWork() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where visibility in DEFAULT_VISIBILITY or UPDATED_VISIBILITY
        defaultStoryShouldBeFound("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY);

        // Get all the storyList where visibility equals to UPDATED_VISIBILITY
        defaultStoryShouldNotBeFound("visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    @Transactional
    public void getAllStoriesByVisibilityIsNullOrNotNull() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);

        // Get all the storyList where visibility is not null
        defaultStoryShouldBeFound("visibility.specified=true");

        // Get all the storyList where visibility is null
        defaultStoryShouldNotBeFound("visibility.specified=false");
    }

    @Test
    @Transactional
    public void getAllStoriesByFragmentIsEqualToSomething() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(storyEntity);
        FragmentEntity fragment = FragmentResourceIT.createEntity(em);
        em.persist(fragment);
        em.flush();
        storyEntity.addFragment(fragment);
        storyRepository.saveAndFlush(storyEntity);
        Long fragmentId = fragment.getId();

        // Get all the storyList where fragment equals to fragmentId
        defaultStoryShouldBeFound("fragmentId.equals=" + fragmentId);

        // Get all the storyList where fragment equals to fragmentId + 1
        defaultStoryShouldNotBeFound("fragmentId.equals=" + (fragmentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStoryShouldBeFound(String filter) throws Exception {
        restStoryMockMvc.perform(get("/api/stories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));

        // Check, that the count call also returns 1
        restStoryMockMvc.perform(get("/api/stories/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStoryShouldNotBeFound(String filter) throws Exception {
        restStoryMockMvc.perform(get("/api/stories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStoryMockMvc.perform(get("/api/stories/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingStory() throws Exception {
        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStory() throws Exception {
        // Initialize the database
        storyService.save(storyEntity);

        int databaseSizeBeforeUpdate = storyRepository.findAll().size();

        // Update the story
        StoryEntity updatedStoryEntity = storyRepository.findById(storyEntity.getId()).get();
        // Disconnect from session so that the updates on updatedStoryEntity are not directly saved in db
        em.detach(updatedStoryEntity);
        updatedStoryEntity
            .category(UPDATED_CATEGORY)
            .name(UPDATED_NAME)
            .visibility(UPDATED_VISIBILITY);

        restStoryMockMvc.perform(put("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedStoryEntity)))
            .andExpect(status().isOk());

        // Validate the Story in the database
        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeUpdate);
        StoryEntity testStory = storyList.get(storyList.size() - 1);
        assertThat(testStory.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testStory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStory.getVisibility()).isEqualTo(UPDATED_VISIBILITY);

        // Validate the Story in Elasticsearch
        verify(mockStorySearchRepository, times(2)).save(testStory);
    }

    @Test
    @Transactional
    public void updateNonExistingStory() throws Exception {
        int databaseSizeBeforeUpdate = storyRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoryMockMvc.perform(put("/api/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(storyEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Story in the database
        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Story in Elasticsearch
        verify(mockStorySearchRepository, times(0)).save(storyEntity);
    }

    @Test
    @Transactional
    public void deleteStory() throws Exception {
        // Initialize the database
        storyService.save(storyEntity);

        int databaseSizeBeforeDelete = storyRepository.findAll().size();

        // Delete the story
        restStoryMockMvc.perform(delete("/api/stories/{id}", storyEntity.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StoryEntity> storyList = storyRepository.findAll();
        assertThat(storyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Story in Elasticsearch
        verify(mockStorySearchRepository, times(1)).deleteById(storyEntity.getId());
    }

    @Test
    @Transactional
    public void searchStory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        storyService.save(storyEntity);
        when(mockStorySearchRepository.search(queryStringQuery("id:" + storyEntity.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(storyEntity), PageRequest.of(0, 1), 1));

        // Search the story
        restStoryMockMvc.perform(get("/api/_search/stories?query=id:" + storyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(storyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY.toString())));
    }
}
