package com.bonlimousin.content.repository.search;

import com.bonlimousin.content.domain.LocalizedEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link LocalizedEntity} entity.
 */
public interface LocalizedSearchRepository extends ElasticsearchRepository<LocalizedEntity, Long> {
}
