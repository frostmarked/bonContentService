package com.bonlimousin.content.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.content.web.rest.TestUtil;

public class LocalizedTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocalizedEntity.class);
        LocalizedEntity localizedEntity1 = new LocalizedEntity();
        localizedEntity1.setId(1L);
        LocalizedEntity localizedEntity2 = new LocalizedEntity();
        localizedEntity2.setId(localizedEntity1.getId());
        assertThat(localizedEntity1).isEqualTo(localizedEntity2);
        localizedEntity2.setId(2L);
        assertThat(localizedEntity1).isNotEqualTo(localizedEntity2);
        localizedEntity1.setId(null);
        assertThat(localizedEntity1).isNotEqualTo(localizedEntity2);
    }
}
