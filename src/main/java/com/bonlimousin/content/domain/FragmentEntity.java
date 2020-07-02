package com.bonlimousin.content.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.bonlimousin.content.domain.enumeration.FragmentTemplate;

import com.bonlimousin.content.domain.enumeration.UserRole;

/**
 * A FragmentEntity.
 */
@Entity
@Table(name = "bon_content_fragment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "fragment")
public class FragmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "template", nullable = false)
    private FragmentTemplate template;

    @NotNull
    @Size(min = 2)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 127)
    @Column(name = "title", length = 127)
    private String title;

    @Size(max = 255)
    @Column(name = "ingress", length = 255)
    private String ingress;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body")
    private String body;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Size(max = 255)
    @Column(name = "caption", length = 255)
    private String caption;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @NotNull
    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private UserRole visibility;

    @OneToMany(mappedBy = "fragment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<LocalizedEntity> localizedFragments = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "bon_content_fragment_tag",
               joinColumns = @JoinColumn(name = "fragment_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<TagEntity> tags = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "fragments", allowSetters = true)
    private StoryEntity story;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FragmentTemplate getTemplate() {
        return template;
    }

    public FragmentEntity template(FragmentTemplate template) {
        this.template = template;
        return this;
    }

    public void setTemplate(FragmentTemplate template) {
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public FragmentEntity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public FragmentEntity title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngress() {
        return ingress;
    }

    public FragmentEntity ingress(String ingress) {
        this.ingress = ingress;
        return this;
    }

    public void setIngress(String ingress) {
        this.ingress = ingress;
    }

    public String getBody() {
        return body;
    }

    public FragmentEntity body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getImage() {
        return image;
    }

    public FragmentEntity image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public FragmentEntity imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getCaption() {
        return caption;
    }

    public FragmentEntity caption(String caption) {
        this.caption = caption;
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getWidth() {
        return width;
    }

    public FragmentEntity width(Integer width) {
        this.width = width;
        return this;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public FragmentEntity height(Integer height) {
        this.height = height;
        return this;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public FragmentEntity orderNo(Integer orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public UserRole getVisibility() {
        return visibility;
    }

    public FragmentEntity visibility(UserRole visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(UserRole visibility) {
        this.visibility = visibility;
    }

    public Set<LocalizedEntity> getLocalizedFragments() {
        return localizedFragments;
    }

    public FragmentEntity localizedFragments(Set<LocalizedEntity> localizeds) {
        this.localizedFragments = localizeds;
        return this;
    }

    public FragmentEntity addLocalizedFragment(LocalizedEntity localized) {
        this.localizedFragments.add(localized);
        localized.setFragment(this);
        return this;
    }

    public FragmentEntity removeLocalizedFragment(LocalizedEntity localized) {
        this.localizedFragments.remove(localized);
        localized.setFragment(null);
        return this;
    }

    public void setLocalizedFragments(Set<LocalizedEntity> localizeds) {
        this.localizedFragments = localizeds;
    }

    public Set<TagEntity> getTags() {
        return tags;
    }

    public FragmentEntity tags(Set<TagEntity> tags) {
        this.tags = tags;
        return this;
    }

    public FragmentEntity addTag(TagEntity tag) {
        this.tags.add(tag);
        tag.getFragments().add(this);
        return this;
    }

    public FragmentEntity removeTag(TagEntity tag) {
        this.tags.remove(tag);
        tag.getFragments().remove(this);
        return this;
    }

    public void setTags(Set<TagEntity> tags) {
        this.tags = tags;
    }

    public StoryEntity getStory() {
        return story;
    }

    public FragmentEntity story(StoryEntity story) {
        this.story = story;
        return this;
    }

    public void setStory(StoryEntity story) {
        this.story = story;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FragmentEntity)) {
            return false;
        }
        return id != null && id.equals(((FragmentEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FragmentEntity{" +
            "id=" + getId() +
            ", template='" + getTemplate() + "'" +
            ", name='" + getName() + "'" +
            ", title='" + getTitle() + "'" +
            ", ingress='" + getIngress() + "'" +
            ", body='" + getBody() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", caption='" + getCaption() + "'" +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            ", orderNo=" + getOrderNo() +
            ", visibility='" + getVisibility() + "'" +
            "}";
    }
}
