package com.bonlimousin.content.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.bonlimousin.content.domain.enumeration.UserRole;

/**
 * A LocalizedEntity.
 */
@Entity
@Table(name = "bon_content_localized")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "localized")
public class LocalizedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Pattern(regexp = "[a-z]+")
    @Column(name = "i_18_n", nullable = false)
    private String i18n;

    @NotNull
    @Size(max = 127)
    @Column(name = "title", length = 127, nullable = false)
    private String title;

    @Size(max = 255)
    @Column(name = "ingress", length = 255)
    private String ingress;

    
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body", nullable = false)
    private String body;

    @Size(max = 255)
    @Column(name = "caption", length = 255)
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private UserRole visibility;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "localizedFragments", allowSetters = true)
    private FragmentEntity fragment;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String geti18n() {
        return i18n;
    }

    public LocalizedEntity i18n(String i18n) {
        this.i18n = i18n;
        return this;
    }

    public void seti18n(String i18n) {
        this.i18n = i18n;
    }

    public String getTitle() {
        return title;
    }

    public LocalizedEntity title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngress() {
        return ingress;
    }

    public LocalizedEntity ingress(String ingress) {
        this.ingress = ingress;
        return this;
    }

    public void setIngress(String ingress) {
        this.ingress = ingress;
    }

    public String getBody() {
        return body;
    }

    public LocalizedEntity body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCaption() {
        return caption;
    }

    public LocalizedEntity caption(String caption) {
        this.caption = caption;
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public UserRole getVisibility() {
        return visibility;
    }

    public LocalizedEntity visibility(UserRole visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(UserRole visibility) {
        this.visibility = visibility;
    }

    public FragmentEntity getFragment() {
        return fragment;
    }

    public LocalizedEntity fragment(FragmentEntity fragment) {
        this.fragment = fragment;
        return this;
    }

    public void setFragment(FragmentEntity fragment) {
        this.fragment = fragment;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalizedEntity)) {
            return false;
        }
        return id != null && id.equals(((LocalizedEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocalizedEntity{" +
            "id=" + getId() +
            ", i18n='" + geti18n() + "'" +
            ", title='" + getTitle() + "'" +
            ", ingress='" + getIngress() + "'" +
            ", body='" + getBody() + "'" +
            ", caption='" + getCaption() + "'" +
            ", visibility='" + getVisibility() + "'" +
            "}";
    }
}
