package com.checkout.models;

import com.google.gson.Gson;

/**
 * Class used for receiving REST messages, it has the same format as the expected response. We extract the useful information based on this.
 */
public class CardTokenResponse {
    private String id;
    private boolean liveMode;
    private String created;
    private boolean used;
    private CardToken card;

    /**
     * Default constructor
     * @param id String containing the card token of the card
     * @param liveMode boolean, if the request was on a live server or not
     * @param created String containing time information about the creation of the card token
     * @param used boolean, if the token has already been used or not
     * @param card CardToken object containing all the information needed to charge the card
     */
    public CardTokenResponse(String id, boolean liveMode, String created, boolean used, CardToken card) {
        this.id = id;
        this.liveMode = liveMode;
        this.created = created;
        this.used = used;
        this.card = card;
    }

    /**
     * Method allowing to create an instance from a JSON representation as a String
     * @param json String containing a JSON instance of this class
     * @return CTResponse instance
     */
    public static CardTokenResponse fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CardTokenResponse.class);
    }

    /**
     * Getter for the card object
     * @return CardToken object filled as received by the server
     */
    public CardToken getCard() {
        return card;
    }

    /**
     * Getter for the card token
     * @return String containing the card token
     */
    public String getCardToken() {
        return id;
    }

    /**
     * Method used for testing purposes, returns this instance as a JSON object
     * @return String containing a JSON representation for this instance
     */
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
