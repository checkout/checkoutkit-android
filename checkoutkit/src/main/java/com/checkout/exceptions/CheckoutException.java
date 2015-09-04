package com.checkout.exceptions;

/**
 * Custom exception raised if any problem occurred in the communication with the server or if the public key is not valid
 */
public class CheckoutException extends Exception {

    /**
     * Enum used to specify the type of the exception and its origin
     */
    public enum CKExceptionType { INVALID_PUBLIC_KEY, API_ERROR, NO_PUBLIC_KEY }

    private CKExceptionType type;

    /**
     * Default constructor. Adds the type of the exception to the message printed when the exception is raised
     * @param type CKExceptionType object containing more precise information about the error
     */
    public CheckoutException(CKExceptionType type) {
        super(type.toString());
        this.type = type;

    }

    public CheckoutException(String message)
    {
        super(message);
    }

    public CheckoutException(Throwable cause)
    {
        super(cause);
    }

    public CheckoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CheckoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}