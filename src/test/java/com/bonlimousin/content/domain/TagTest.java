package com.bonlimousin.content.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.content.web.rest.TestUtil;

public class TagTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagEntity.class);
        TagEntity tagEntity1 = new TagEntity();
        tagEntity1.setId(1L);
        TagEntity tagEntity2 = new TagEntity();
        tagEntity2.setId(tagEntity1.getId());
        assertThat(tagEntity1).isEqualTo(tagEntity2);
        tagEntity2.setId(2L);
        assertThat(tagEntity1).isNotEqualTo(tagEntity2);
        tagEntity1.setId(null);
        assertThat(tagEntity1).isNotEqualTo(tagEntity2);
    }
}
