package com.bonlimousin.content.repository.search;

import com.bonlimousin.content.domain.TagEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link TagEntity} entity.
 */
public interface TagSearchRepository extends ElasticsearchRepository<TagEntity, Long> {
}
