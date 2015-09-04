package com.checkout.models;

import com.checkout.CardValidator;
import com.checkout.CardValidator.Cards;
import com.checkout.exceptions.CardException;
import com.checkout.exceptions.CardException.CardExceptionType;

/**
 * Class containing the card's details before sending them to createCardToken
 */
public class Card {

    private String number;
    private String name;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private CustDetails billingDetails;

    /**
     * Default constructor
     * @param cardNumber String containing the card's number
     * @param name String containing the card's owner name
     * @param expMonth String containing the expiry month
     * @param expYear String containing the expiry year
     * @param cvv String containing the CVV
     * @throws CardException if any of the parameter is not valid
     */
    public Card(String cardNumber, String name, String expMonth, String expYear, String cvv) throws CardException {
        this(cardNumber, name, expMonth, expYear, cvv, null);
    }

    /**
     * Secondary constructor, with optional billing details
     * @param cardNumber String containing the card's number
     * @param name String containing the card's owner name
     * @param expMonth String containing the expiry month
     * @param expYear String containing the expiry year
     * @param cvv String containing the CVV
     * @param billingDetails CustDetails object containing the customer details
     * @throws CardException if any of the parameter is not valid
     */
    public Card(String cardNumber, String name, String expMonth, String expYear, String cvv, CustDetails billingDetails) throws CardException {
        if (!CardValidator.validateCardNumber(cardNumber)) throw new CardException(CardExceptionType.INVALID_NUMBER);
        if (!CardValidator.validateExpiryDate(expMonth, expYear)) throw new CardException(CardExceptionType.INVALID_EXPIRY_DATE);
        Cards card = CardValidator.getCardType(cardNumber);
        if (!CardValidator.validateCVV(cvv, card)) throw new CardException(CardExceptionType.INVALID_CVV);

        this.number = CardValidator.sanitizeEntry(cardNumber, true);
        this.name = name;
        this.expiryMonth = expMonth;
        this.expiryYear = expYear;
        this.cvv = cvv;
        this.billingDetails = billingDetails;
    }

    /**
     * Setter for the card number
     * @param number String containing the new card number
     * @throws CardException if the card number is not valid
     */
    public void setNumber(String number) throws CardException {
        if (!CardValidator.validateCardNumber(number)) throw new CardException(CardExceptionType.INVALID_NUMBER);
        else this.number = CardValidator.sanitizeEntry(number, true);
    }

    /**
     * Setter for the name
     * @param name String containing the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the expiry month and the expiry year
     * @param expiryMonth String containing the new expiry month
     * @param expiryYear String containing the new expiry year
     * @throws CardException if the expiry date is not valid
     */
    public void setExpiryDate(String expiryMonth, String expiryYear) throws CardException {
        if (!CardValidator.validateExpiryDate(expiryMonth, expiryYear)) throw new CardException(CardExceptionType.INVALID_EXPIRY_DATE);
        else {
            this.expiryMonth = expiryMonth;
            this.expiryYear = expiryYear;
        }
    }

    /**
     * Setter for the CVV
     * @param cvv String containing the new cvv
     * @throws CardException if the cvv is not valid
     */
    public void setCvv(String cvv) throws CardException {
        if (this.number != null) {
            Cards card = CardValidator.getCardType(this.number);
            if (!CardValidator.validateCVV(cvv, card))
                throw new CardException(CardExceptionType.INVALID_CVV);
            else this.cvv = cvv;
        } else throw new CardException(CardExceptionType.INVALID_NUMBER);
    }

    /**
     * Setter for the billing details
     * @param billingDetails CustDetails instance with the new biling details
     */
    public void setBillingDetails(CustDetails billingDetails) {
        this.billingDetails = billingDetails;
    }
}
