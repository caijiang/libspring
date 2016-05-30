package org.luffy.test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = SpringWebTestTest.Nothing.class)
public class SpringWebTestTest extends me.jiangcai.lib.test.SpringWebTest {

    @Override
    public void prepareFields() {
    }

    @Override
    public void createMockMVC() {
    }

    @Test
    public void testRandomArray() throws Exception {
        //0 1 and many
        doRandomArray(0);
        doRandomArray(1);
        doRandomArray(random.nextInt(88) + 2);
    }

    private void doRandomArray(int length) {
        String[] array = new String[length];
        Arrays.fill(array, UUID.randomUUID().toString());
        //min 从0到length
        for (int i = 0; i < length; i++) {
            String[] newArray = randomArray(array, i);
            assertThat(newArray.length)
                    .isGreaterThanOrEqualTo(i)
                    .isLessThanOrEqualTo(length);
        }
    }

    @Test
    public void randomTest() {
        int x = 50;
        while (x-- > 0) {
            System.out.println(randomMobile());
            System.out.println(randomEmailAddress());
            System.out.println(randomHttpURL());
        }
    }

    public static class Nothing {

    }
}