package com.checkout.models;

import java.util.List;

/**
 * Class used for receiving REST messages, it has the same format as the expected response. We extract the useful information based on this.
 */
public class CardProviderResponse {

    private String object;
    private int count;
    private List<CardProvider> data;

    /**
     * Default constructor
     * @param object String containing type of the JSON data
     * @param count int containing the number of elements in the JSON data
     * @param data String containing the JSON data
     */
    public CardProviderResponse(String object, int count, List<CardProvider> data) {
        this.object = object;
        this.count = count;
        this.data = data;
    }

    /**
     * Getter for the JSON data as a Java object
     * @return Object instance corresponding of the returned JSON response
     */
    public List<CardProvider> getData() {
        return data;
    }

}
