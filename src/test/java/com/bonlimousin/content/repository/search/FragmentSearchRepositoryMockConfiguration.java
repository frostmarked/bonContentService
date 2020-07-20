package com.bonlimousin.content.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FragmentSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FragmentSearchRepositoryMockConfiguration {

    @MockBean
    private FragmentSearchRepository mockFragmentSearchRepository;

}
