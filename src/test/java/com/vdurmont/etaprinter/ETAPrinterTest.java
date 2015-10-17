package com.vdurmont.etaprinter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ETAPrinterTest {
    @Test
    public void update_with_an_itemDuration_lower_than_1ms_doesnt_fail() {
        // GIVEN
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        ETAPrinter eta = ETAPrinter.init(42);

        // WHEN
        DateTimeUtils.setCurrentMillisFixed(now.getMillis() + 1);
        eta.update(2);

        // THEN
        // No failure
    }
}