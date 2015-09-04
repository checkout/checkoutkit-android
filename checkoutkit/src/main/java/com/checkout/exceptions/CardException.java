package com.checkout.exceptions;

/**
 * Custom exception raised when any of the card's details is not valid
 */
public class CardException extends Exception {

    /**
     * Enum used to specify the type of the exception and its origin
     */
    public enum CardExceptionType { INVALID_CVV, INVALID_EXPIRY_DATE, INVALID_NUMBER }

    private CardExceptionType type;

    /**
     * Default constructor. Adds the type of the exception to the message printed when the exception is raised
     * @param type CardExceptionType object containing more precise information about the error
     */
    public CardException(CardExceptionType type) {
        super(type.toString());
        this.type = type;
    }

    public CardException(String message)
    {
        super(message);
    }

    public CardException(Throwable cause)
    {
        super(cause);
    }

    public CardException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CardExceptionType getType() {
        return type;
    }
}
