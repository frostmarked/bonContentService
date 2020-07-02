package com.bonlimousin.content.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.content.web.rest.TestUtil;

public class FragmentTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FragmentEntity.class);
        FragmentEntity fragmentEntity1 = new FragmentEntity();
        fragmentEntity1.setId(1L);
        FragmentEntity fragmentEntity2 = new FragmentEntity();
        fragmentEntity2.setId(fragmentEntity1.getId());
        assertThat(fragmentEntity1).isEqualTo(fragmentEntity2);
        fragmentEntity2.setId(2L);
        assertThat(fragmentEntity1).isNotEqualTo(fragmentEntity2);
        fragmentEntity1.setId(null);
        assertThat(fragmentEntity1).isNotEqualTo(fragmentEntity2);
    }
}
