package com.bonlimousin.content.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.content.web.rest.TestUtil;

public class StoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryEntity.class);
        StoryEntity storyEntity1 = new StoryEntity();
        storyEntity1.setId(1L);
        StoryEntity storyEntity2 = new StoryEntity();
        storyEntity2.setId(storyEntity1.getId());
        assertThat(storyEntity1).isEqualTo(storyEntity2);
        storyEntity2.setId(2L);
        assertThat(storyEntity1).isNotEqualTo(storyEntity2);
        storyEntity1.setId(null);
        assertThat(storyEntity1).isNotEqualTo(storyEntity2);
    }
}
