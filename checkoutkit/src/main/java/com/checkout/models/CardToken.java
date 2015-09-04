package com.checkout.models;

import com.google.gson.Gson;

/**
 * Class instantiated when the createCardToken method of CheckoutKit is called, it contains all the information returned by Checkout
 */
public class CardToken {


    private String expiryMonth;
    private String expiryYear;
    private CustDetails billDetails;
    private String id;
    private String last4;
    private String paymentMethod;
    private String fingerprint;
    private String name;

    /**
     * Default constructor for a CardToken
     * @param expiryMonth String containing the expiring month of the card
     * @param expiryYear String containing the expiring year of the card
     * @param billDetails Object containing the billing details of the customer
     * @param id String containing the id of the card
     * @param last4 String containing the last 4 digits of the card's number
     * @param paymentMethod String containing the payment method corresponding to the card
     * @param fingerprint String containing the fingerprint corresponding to the card
     * @param name String corresponding the the card's owner name
     */
    public CardToken(String expiryMonth, String expiryYear, CustDetails billDetails, String id, String last4, String paymentMethod, String fingerprint, String name) {
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.billDetails = billDetails;
        this.id = id;
        this.last4 = last4;
        this.paymentMethod = paymentMethod;
        this.fingerprint = fingerprint;
        this.name = name;
    }

    /**
     * Getter for the expiry month
     * @return String containing the expiry month
     */
    public String getExpiryMonth() {
        return expiryMonth;
    }

    /**
     * Getter for the expiry year
     * @return String containing the expiry year
     */
    public String getExpiryYear() {
        return expiryYear;
    }

    /**
     * Getter for the customer billing details
     * @return CustDetails object containing the billing details
     */
    public CustDetails getBillDetails() {
        return billDetails;
    }

    /**
     * Getter for the card's id
     * @return String containing the card's id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the 4 last digits of the card's number
     * @return String containing the 4 lasts digits of the card's number
     */
    public String getLast4() {
        return last4;
    }

    /**
     * Getter for the payment method
     * @return String containing the payment method corresponding to the card
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Getter for the card's fingerprint
     * @return String containing the card's fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * Getter for the card's owner name
     * @return String containing the name of the customer
     */
    public String getName() {
        return name;
    }

    /**
     * Method returning the Json representation of this instance as a String
     * @return String containing the Json representation
     */
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Redefinition of equals for the unit tests
     * @param o Object to be compared to
     * @return boolean, if the objects are the same or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardToken cardToken1 = (CardToken) o;

        if (expiryMonth != null ? !expiryMonth.equals(cardToken1.expiryMonth) : cardToken1.expiryMonth != null)
            return false;
        if (expiryYear != null ? !expiryYear.equals(cardToken1.expiryYear) : cardToken1.expiryYear != null)
            return false;
        if (billDetails != null ? !billDetails.equals(cardToken1.billDetails) : cardToken1.billDetails != null)
            return false;
        if (id != null ? !id.equals(cardToken1.id) : cardToken1.id != null) return false;
        if (last4 != null ? !last4.equals(cardToken1.last4) : cardToken1.last4 != null)
            return false;
        if (paymentMethod != null ? !paymentMethod.equals(cardToken1.paymentMethod) : cardToken1.paymentMethod != null)
            return false;
        if (fingerprint != null ? !fingerprint.equals(cardToken1.fingerprint) : cardToken1.fingerprint != null)
            return false;
        if (name != null ? !name.equals(cardToken1.name) : cardToken1.name != null) return false;
        return true;
    }
}
