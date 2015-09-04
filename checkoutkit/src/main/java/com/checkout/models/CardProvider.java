package com.checkout.models;

/**
 * Class used to represent a card provider
 */
public class CardProvider {
    private String id;
    private String name;
    private boolean cvvRequired;

    /**
     * Default constructor
     * @param id String containing the id of the card provider
     * @param name String containing the name of the card provider
     * @param cvvRequired boolean, if the cvv is required for this card provider
     */
    public CardProvider(String id, String name, boolean cvvRequired) {
        this.id = id;
        this.name = name;
        this.cvvRequired = cvvRequired;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + cvvRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardProvider that = (CardProvider) o;

        if (cvvRequired != that.cvvRequired) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }
}
