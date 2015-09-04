package com.checkout.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class used to log activity in the console
 */
public class Log {

    private static Log log = null;

    /*
     * Default empty constructor
     */
    private Log() {
    }

    /**
     * Static method returning the log instance (or creating the log if it is the first call), part of the singleton pattern
     * @return unique Log instance
     */
    public static Log getLog() {
        if (log == null) {
            log = new Log();
        }
        return log;
    }

    /*
     * Private method used to print the current date in the console
     */
    private void printDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
    }

    /**
     * Prints an information message in the console and when it occurred
     * @param message String containing the message to log
     */
    public void info(String message) {
        printDate();
        String toPrint = "INFO: " + message;
        System.out.println(toPrint);
    }

    /**
     * Prints a warning message in the console and when it occurred
     * @param message String containing the message to log
     */
    public void warn(String message) {
        printDate();
        String toPrint = "WARNING: " + message;
        System.out.println(toPrint);
    }

    /**
     * Prints an error message in the console and when it occurred
     * @param message String containing the message to log
     */
    public void error(String message) {
        printDate();
        String toPrint = "ERROR: " + message;
        System.out.println(toPrint);
    }
}
