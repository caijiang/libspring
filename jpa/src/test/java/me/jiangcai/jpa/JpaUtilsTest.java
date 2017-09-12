package me.jiangcai.jpa;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class JpaUtilsTest {
    @Test
    public void idClassForEntity() throws Exception {
        assertThat(JpaUtils.idClassForEntity(EntityTest1.class))
                .isEqualTo(String.class);
        assertThat(JpaUtils.idClassForEntity(EntityTest2.class))
                .isEqualTo(String.class);
    }

}