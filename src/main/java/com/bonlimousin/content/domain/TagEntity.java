package com.bonlimousin.content.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TagEntity.
 */
@Entity
@Table(name = "bon_content_tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tag")
public class TagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<FragmentEntity> fragments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TagEntity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<FragmentEntity> getFragments() {
        return fragments;
    }

    public TagEntity fragments(Set<FragmentEntity> fragments) {
        this.fragments = fragments;
        return this;
    }

    public TagEntity addFragment(FragmentEntity fragment) {
        this.fragments.add(fragment);
        fragment.getTags().add(this);
        return this;
    }

    public TagEntity removeFragment(FragmentEntity fragment) {
        this.fragments.remove(fragment);
        fragment.getTags().remove(this);
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
        if (!(o instanceof TagEntity)) {
            return false;
        }
        return id != null && id.equals(((TagEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagEntity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
