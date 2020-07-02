package com.bonlimousin.content.web.rest;

import com.bonlimousin.content.BonContentServiceApp;
import com.bonlimousin.content.domain.TagEntity;
import com.bonlimousin.content.domain.FragmentEntity;
import com.bonlimousin.content.repository.TagRepository;
import com.bonlimousin.content.repository.search.TagSearchRepository;
import com.bonlimousin.content.service.TagService;
import com.bonlimousin.content.service.dto.TagCriteria;
import com.bonlimousin.content.service.TagQueryService;

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

/**
 * Integration tests for the {@link TagResource} REST controller.
 */
@SpringBootTest(classes = BonContentServiceApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class TagResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    /**
     * This repository is mocked in the com.bonlimousin.content.repository.search test package.
     *
     * @see com.bonlimousin.content.repository.search.TagSearchRepositoryMockConfiguration
     */
    @Autowired
    private TagSearchRepository mockTagSearchRepository;

    @Autowired
    private TagQueryService tagQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagMockMvc;

    private TagEntity tagEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagEntity createEntity(EntityManager em) {
        TagEntity tagEntity = new TagEntity()
            .name(DEFAULT_NAME);
        return tagEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagEntity createUpdatedEntity(EntityManager em) {
        TagEntity tagEntity = new TagEntity()
            .name(UPDATED_NAME);
        return tagEntity;
    }

    @BeforeEach
    public void initTest() {
        tagEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();
        // Create the Tag
        restTagMockMvc.perform(post("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tagEntity)))
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate + 1);
        TagEntity testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(1)).save(testTag);
    }

    @Test
    @Transactional
    public void createTagWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag with an existing ID
        tagEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagMockMvc.perform(post("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tagEntity);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tagEntity.setName(null);

        // Create the Tag, which fails.


        restTagMockMvc.perform(post("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTags() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList
        restTagMockMvc.perform(get("/api/tags?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", tagEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tagEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }


    @Test
    @Transactional
    public void getTagsByIdFiltering() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        Long id = tagEntity.getId();

        defaultTagShouldBeFound("id.equals=" + id);
        defaultTagShouldNotBeFound("id.notEquals=" + id);

        defaultTagShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTagShouldNotBeFound("id.greaterThan=" + id);

        defaultTagShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTagShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllTagsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name equals to DEFAULT_NAME
        defaultTagShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the tagList where name equals to UPDATED_NAME
        defaultTagShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name not equals to DEFAULT_NAME
        defaultTagShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the tagList where name not equals to UPDATED_NAME
        defaultTagShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTagShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the tagList where name equals to UPDATED_NAME
        defaultTagShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name is not null
        defaultTagShouldBeFound("name.specified=true");

        // Get all the tagList where name is null
        defaultTagShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllTagsByNameContainsSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name contains DEFAULT_NAME
        defaultTagShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the tagList where name contains UPDATED_NAME
        defaultTagShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTagsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name does not contain DEFAULT_NAME
        defaultTagShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the tagList where name does not contain UPDATED_NAME
        defaultTagShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllTagsByFragmentIsEqualToSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagEntity);
        FragmentEntity fragment = FragmentResourceIT.createEntity(em);
        em.persist(fragment);
        em.flush();
        tagEntity.addFragment(fragment);
        tagRepository.saveAndFlush(tagEntity);
        Long fragmentId = fragment.getId();

        // Get all the tagList where fragment equals to fragmentId
        defaultTagShouldBeFound("fragmentId.equals=" + fragmentId);

        // Get all the tagList where fragment equals to fragmentId + 1
        defaultTagShouldNotBeFound("fragmentId.equals=" + (fragmentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTagShouldBeFound(String filter) throws Exception {
        restTagMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restTagMockMvc.perform(get("/api/tags/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTagShouldNotBeFound(String filter) throws Exception {
        restTagMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTagMockMvc.perform(get("/api/tags/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTag() throws Exception {
        // Initialize the database
        tagService.save(tagEntity);

        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag
        TagEntity updatedTagEntity = tagRepository.findById(tagEntity.getId()).get();
        // Disconnect from session so that the updates on updatedTagEntity are not directly saved in db
        em.detach(updatedTagEntity);
        updatedTagEntity
            .name(UPDATED_NAME);

        restTagMockMvc.perform(put("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTagEntity)))
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        TagEntity testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(2)).save(testTag);
    }

    @Test
    @Transactional
    public void updateNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagMockMvc.perform(put("/api/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(0)).save(tagEntity);
    }

    @Test
    @Transactional
    public void deleteTag() throws Exception {
        // Initialize the database
        tagService.save(tagEntity);

        int databaseSizeBeforeDelete = tagRepository.findAll().size();

        // Delete the tag
        restTagMockMvc.perform(delete("/api/tags/{id}", tagEntity.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TagEntity> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Tag in Elasticsearch
        verify(mockTagSearchRepository, times(1)).deleteById(tagEntity.getId());
    }

    @Test
    @Transactional
    public void searchTag() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        tagService.save(tagEntity);
        when(mockTagSearchRepository.search(queryStringQuery("id:" + tagEntity.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tagEntity), PageRequest.of(0, 1), 1));

        // Search the tag
        restTagMockMvc.perform(get("/api/_search/tags?query=id:" + tagEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
