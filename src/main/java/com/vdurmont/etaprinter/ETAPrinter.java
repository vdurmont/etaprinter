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
    private boolean closed;
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
        this.closed = false;
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
        if (this.closed) {
            throw new ETAPrinterException("You cannot update a closed ETAPrinter!");
        }
        DateTime now = DateTime.now();
        ItemsDuration itemsDuration = null;
        if (numProcessedDuringStep > 0) {
            long stepDuration = new Duration(this.lastStep, now).getMillis();
            int numItems = 1;
            do {
                long duration = stepDuration / (numProcessedDuringStep / numItems);
                itemsDuration = new ItemsDuration(numItems, duration);
                numItems++;
            } while (itemsDuration.durationMillis == 0);
        }
        this.lastStep = now;
        this.numProcessed += numProcessedDuringStep;
        if (this.numProcessed == this.totalElementsToProcess) {
            this.close();
        } else {
            this.print(itemsDuration);
        }
    }

    private void print(ItemsDuration itemsDuration) {
        try {
            // Get the status
            String status = this.getStatus(itemsDuration);

            // Carriage return and print the status
            this.stream.write("\r".getBytes());
            this.stream.write(status.getBytes());
        } catch (IOException e) {
            throw new ETAPrinterException("An error occurred while printing the status", e);
        }
    }

    private void close() {
        this.closed = true;
        try {
            String endString = "\r" + ETAStatusGenerator.getStatus(100) + " Complete.\n";
            this.stream.write(endString.getBytes());
            if (this.closeStream) {
                this.stream.close();
            }
        } catch (IOException e) {
            throw new ETAPrinterException("An error occurred while printing the status", e);
        }
    }

    private String getStatus(ItemsDuration itemsDuration) {
        long percentage = this.numProcessed * 100 / this.totalElementsToProcess;
        if (itemsDuration == null) {
            return ETAStatusGenerator.getStatus(percentage);
        } else {
            long etaMillis = (this.totalElementsToProcess - this.numProcessed) * itemsDuration.durationMillis / itemsDuration.numItems;
            double speed = 0;
            int index = 0;
            int speedFactor = 1;
            do {
                speedFactor *= SPEED_FACTORS[index];
                speed = speedFactor * itemsDuration.numItems / itemsDuration.durationMillis;
                index++;
            } while (speed == 0 && index < SPEED_UNITS.length);

            Duration eta = new Duration(etaMillis);
            return ETAStatusGenerator.getStatus(this.elementName, percentage, speed, SPEED_UNITS[index - 1], eta);
        }
    }

    private static class ItemsDuration {
        public final int numItems;
        public final long durationMillis;

        private ItemsDuration(int numItems, long durationMillis) {
            this.numItems = numItems;
            this.durationMillis = durationMillis;
        }
    }
}