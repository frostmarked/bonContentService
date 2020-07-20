package com.bonlimousin.content.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.bonlimousin.content.domain.enumeration.StoryCategory;

import com.bonlimousin.content.domain.enumeration.UserRole;

/**
 * A StoryEntity.
 */
@Entity
@Table(name = "bon_content_story")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "story")
public class StoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private StoryCategory category;

    @NotNull
    @Size(min = 2)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private UserRole visibility;

    @OneToMany(mappedBy = "story")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<FragmentEntity> fragments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryCategory getCategory() {
        return category;
    }

    public StoryEntity category(StoryCategory category) {
        this.category = category;
        return this;
    }

    public void setCategory(StoryCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public StoryEntity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getVisibility() {
        return visibility;
    }

    public StoryEntity visibility(UserRole visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(UserRole visibility) {
        this.visibility = visibility;
    }

    public Set<FragmentEntity> getFragments() {
        return fragments;
    }

    public StoryEntity fragments(Set<FragmentEntity> fragments) {
        this.fragments = fragments;
        return this;
    }

    public StoryEntity addFragment(FragmentEntity fragment) {
        this.fragments.add(fragment);
        fragment.setStory(this);
        return this;
    }

    public StoryEntity removeFragment(FragmentEntity fragment) {
        this.fragments.remove(fragment);
        fragment.setStory(null);
        return this;
    }

    public void setFragments(Set<FragmentEntity> fragments) {
        this.fragments = fragments;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryEntity)) {
            return false;
        }
        return id != null && id.equals(((StoryEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoryEntity{" +
            "id=" + getId() +
            ", category='" + getCategory() + "'" +
            ", name='" + getName() + "'" +
            ", visibility='" + getVisibility() + "'" +
            "}";
    }
}
