package com.checkout.httpconnector;

import java.util.List;

/**
 * Class used to represent a error returned by the server after a request has failed. It contains all the information relative to this error/failure.
 */

public class ResponseError {

    public String errorCode;
    public String message;
    public String eventId;
    public List<String> errors;

    /**
     * Default constructor used by Gson to build the response automatically based on JSON data
     * @param errorCode String containing the error code if an error occurred
     * @param message String containing the message describing the error
     * @param eventId String containing the event id that triggered the error
     * @param errors List of String containing more information on the errors that occurred
     */
    public ResponseError(String errorCode, String message, String eventId, List<String> errors) {
        this.errorCode = errorCode;
        this.message = message;
        this.eventId = eventId;
        this.errors = errors;
    }
}