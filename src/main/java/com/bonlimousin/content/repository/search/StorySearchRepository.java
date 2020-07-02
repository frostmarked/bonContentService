package com.bonlimousin.content.repository.search;

import com.bonlimousin.content.domain.StoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link StoryEntity} entity.
 */
public interface StorySearchRepository extends ElasticsearchRepository<StoryEntity, Long> {
}
