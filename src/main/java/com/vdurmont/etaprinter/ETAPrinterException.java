package com.vdurmont.etaprinter;

/**
 * The exception used in the {@link com.vdurmont.etaprinter.ETAPrinter}
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class ETAPrinterException extends RuntimeException {
    /**
     * Constructor for the {@link com.vdurmont.etaprinter.ETAPrinterException}
     *
     * @param message   the message for the error
     * @param throwable the underlying exception
     */
    public ETAPrinterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}