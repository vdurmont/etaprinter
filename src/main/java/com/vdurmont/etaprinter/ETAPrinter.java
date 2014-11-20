package com.vdurmont.etaprinter;


import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The {@link com.vdurmont.etaprinter.ETAPrinter} is used to display and update a progress bar when processing
 * a batch of elements.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class ETAPrinter {
    ////////////////////////
    // VARIABLES
    ////////////////

    private static final String[] SPEED_UNITS = {"s", "mn", "h", "d"};
    private static final int[] SPEED_FACTORS = {1000, 60, 60, 24};

    private final String elementName;
    private final OutputStream stream;
    private final boolean closeStream;
    private final long totalElementsToProcess;
    private DateTime lastStep;
    private long numProcessed;

    ////////////////////////
    // CONSTRUCTORS
    ////////////////

    private ETAPrinter(String elementName, long totalElementsToProcess, DateTime startDate, OutputStream stream, boolean closeStream) {
        this.elementName = elementName;
        this.stream = stream;
        this.totalElementsToProcess = totalElementsToProcess;
        this.lastStep = startDate;
        this.numProcessed = 0;
        this.closeStream = closeStream;
    }

    /**
     * Initializes a progress bar.
     * {@link java.lang.System}.out will be used to print the progress bar.
     *
     * @param totalElementsToProcess the total number of items that are going to be processed.
     *
     * @return the initialized {@link com.vdurmont.etaprinter.ETAPrinter}
     */
    public static ETAPrinter init(long totalElementsToProcess) {
        return init(totalElementsToProcess, System.out, false);
    }

    /**
     * Initializes a progress bar.
     *
     * @param totalElementsToProcess the total number of items that are going to be processed.
     * @param stream                 the {@link java.io.OutputStream} where the progress bar will be printed
     * @param closeStream            true if the stream has to be closed at the end of the process
     *
     * @return the initialized {@link com.vdurmont.etaprinter.ETAPrinter}
     */
    public static ETAPrinter init(long totalElementsToProcess, OutputStream stream, boolean closeStream) {
        return init(null, totalElementsToProcess, stream, closeStream);
    }

    /**
     * Initializes a progress bar.
     * {@link java.lang.System}.out will be used to print the progress bar.
     *
     * @param elementName            the name of the elements that are processed. It is used to display the speed.
     * @param totalElementsToProcess the total number of items that are going to be processed.
     *
     * @return the initialized {@link com.vdurmont.etaprinter.ETAPrinter}
     */
    public static ETAPrinter init(String elementName, long totalElementsToProcess) {
        return init(elementName, totalElementsToProcess, System.out, false);
    }

    /**
     * Initializes a progress bar.
     *
     * @param elementName            the name of the elements that are processed. It is used to display the speed.
     * @param totalElementsToProcess the total number of items that are going to be processed.
     * @param stream                 the {@link java.io.OutputStream} where the progress bar will be printed
     * @param closeStream            true if the stream has to be closed at the end of the process
     *
     * @return the initialized {@link com.vdurmont.etaprinter.ETAPrinter}
     */
    public static ETAPrinter init(String elementName, long totalElementsToProcess, OutputStream stream, boolean closeStream) {
        ETAPrinter printer = new ETAPrinter(elementName, totalElementsToProcess, DateTime.now(), stream, closeStream);
        printer.update(0);
        return printer;
    }

    ////////////////////////
    // METHODS
    ////////////////

    /**
     * Updates and prints the progress bar with the new values.
     *
     * @param numProcessedDuringStep the number of items processed during the elapsed step
     */
    public void update(long numProcessedDuringStep) {
        DateTime now = DateTime.now();
        Long itemDurationMillis = null;
        if (numProcessedDuringStep > 0) {
            itemDurationMillis = new Duration(this.lastStep, now).getMillis() / numProcessedDuringStep;
        }
        this.lastStep = now;
        this.numProcessed += numProcessedDuringStep;
        if (this.numProcessed == this.totalElementsToProcess) {
            this.close();
        } else {
            this.print(itemDurationMillis);
        }
    }

    private void print(Long itemDurationMillis) {
        try {
            // Get the status
            String status = this.getStatus(itemDurationMillis);

            // Carriage return and print the status
            this.stream.write("\r".getBytes());
            this.stream.write(status.getBytes());
        } catch (IOException e) {
            throw new ETAPrinterException("An error occurred while printing the status", e);
        }
    }

    /**
     * Prints the last progress bar and closes the stream if needed.
     */
    public void close() {
        try {
            String endString = ETAStatusGenerator.getStatus(100) + " Complete.\n";
            this.stream.write(endString.getBytes());
            if (this.closeStream) {
                this.stream.close();
            }
        } catch (IOException e) {
            throw new ETAPrinterException("An error occurred while printing the status", e);
        }
    }

    private String getStatus(Long itemDurationMillis) {
        long percentage = this.numProcessed * 100 / this.totalElementsToProcess;
        if (itemDurationMillis == null) {
            return ETAStatusGenerator.getStatus(percentage);
        } else {
            long etaMillis = (this.totalElementsToProcess - this.numProcessed) * itemDurationMillis;
            double speed;
            int index = 0;
            int speedFactor = 1;
            do {
                speedFactor *= SPEED_FACTORS[index];
                speed = speedFactor / itemDurationMillis;
                index++;
            } while (speed == 0 && index < SPEED_UNITS.length);

            Duration eta = new Duration(etaMillis);
            return ETAStatusGenerator.getStatus(this.elementName, percentage, speed, SPEED_UNITS[index - 1], eta);
        }
    }
}