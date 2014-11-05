package com.vdurmont.etaprinter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ETAStatusGeneratorTest {
    @Test
    public void getBar_at_0_percent() {
        // GIVEN

        // WHEN
        String actualBar = ETAStatusGenerator.getBar(0);

        // THEN
        String expectedBar = "0% [" + generate(" ", 100) + "] ";
        assertEquals(expectedBar, actualBar);
    }

    @Test
    public void getBar_at_1_percent() {
        // GIVEN

        // WHEN
        String actualBar = ETAStatusGenerator.getBar(1);

        // THEN
        String expectedBar = "1% [>" + generate(" ", 99) + "] ";
        assertEquals(expectedBar, actualBar);
    }

    @Test
    public void getBar_at_3_percent() {
        // GIVEN

        // WHEN
        String actualBar = ETAStatusGenerator.getBar(3);

        // THEN
        String expectedBar = "3% [==>" + generate(" ", 97) + "] ";
        assertEquals(expectedBar, actualBar);
    }

    @Test
    public void getBar_at_99_percent() {
        // GIVEN

        // WHEN
        String actualBar = ETAStatusGenerator.getBar(99);

        // THEN
        String expectedBar = "99% [" + generate("=", 98) + "> ] ";
        assertEquals(expectedBar, actualBar);
    }

    @Test
    public void getBar_at_100_percent() {
        // GIVEN

        // WHEN
        String actualBar = ETAStatusGenerator.getBar(100);

        // THEN
        String expectedBar = "100% [" + generate("=", 100) + "] ";
        assertEquals(expectedBar, actualBar);
    }

    private static String generate(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}