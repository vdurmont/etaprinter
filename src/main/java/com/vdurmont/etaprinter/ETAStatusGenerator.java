package com.vdurmont.etaprinter;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Tools to generate the strings to print the progress bar and the ETA info.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class ETAStatusGenerator {
    private static final PeriodFormatter FORMATTER = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix("d")
            .appendHours()
            .appendSuffix("h")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .toFormatter();

    /**
     * Generate the status when we only have a percentage.
     *
     * @param percentage the progression percentage
     *
     * @return the status string
     */
    public static String getStatus(long percentage) {
        return getBar(percentage);
    }

    /**
     * Generate the full status.
     *
     * @param elementName the optional name of the element that is processed
     * @param percentage  the progression percentage
     * @param speed       the speed of the processing (in number of element by speed unit)
     * @param speedUnit   the unit of the given speed
     * @param eta         the estimated remaining duration for the processing
     *
     * @return the status string
     */
    public static String getStatus(String elementName, long percentage, double speed, String speedUnit, Duration eta) {
        StringBuilder sb = new StringBuilder(getBar(percentage));
        sb.append((long) speed);
        if (elementName != null) {
            sb.append(" ").append(elementName);
        }
        sb.append("/").append(speedUnit);
        sb.append(" ETA ").append(FORMATTER.print(eta.toPeriod()));
        return sb.toString();
    }

    /**
     * Generates the progress bar for the given percentage
     *
     * @param percentage the progression percentage
     *
     * @return the progress bar as a string
     */
    protected static String getBar(long percentage) {
        StringBuilder sb = new StringBuilder();
        sb.append(percentage);
        sb.append("% [");
        for (int i = 0; i < 100; i++) {
            if (percentage == 100 || i < percentage - 1) {
                sb.append("=");
            } else if (i == percentage - 1) {
                sb.append(">");
            } else {
                sb.append(" ");
            }
        }
        sb.append("] ");
        return sb.toString();
    }
}