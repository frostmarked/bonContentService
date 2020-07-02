package com.bonlimousin.content.repository.search;

import com.bonlimousin.content.domain.FragmentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link FragmentEntity} entity.
 */
public interface FragmentSearchRepository extends ElasticsearchRepository<FragmentEntity, Long> {
}
