package com.checkout.models;

/**
 * Class used to represent customer's details
 */
public class CustDetails {

    private String address1;
    private String address2;
    private String postCode;
    private String country;
    private String city;
    private String state;
    private Phone phone;

    /**
     * Default constructor
     * @param address1 String containing the first line of the customer's address
     * @param address2 String containing the second line of the customer's address
     * @param postCode String containing the postal code of the customer's address
     * @param country String containing the country of the customer's address
     * @param city String containing the city of the customer's address
     * @param state String containing the state of the customer's address
     * @param phoneCountryCode String containing the country code of the customer
     * @param phoneNumber String containing the phone number of the customer
     */
    public CustDetails(String address1, String address2, String postCode, String country, String city, String state, String phoneCountryCode, String phoneNumber) {
        this.address1 = address1;
        this.address2 = address2;
        this.postCode = postCode;
        this.country = country;
        this.city = city;
        this.state = state;
        this.phone = new Phone(phoneCountryCode, phoneNumber);
    }

    /**
     * Getter for the customer's address
     * @return String containing the first line of the customer's address
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Getter for the customer's address
     * @return String containing the second line of the customer's address
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Getter for the customer's postal code
     * @return String containing the postal code of the customer's address
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Getter for the customer's country
     * @return String containing the country of the customer's address
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter for the customer's city
     * @return String containing the city of the customer's address
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter for the customer's state
     * @return String containing the state of the customer's address
     */
    public String getState() {
        return state;
    }

    /**
     * Getter for the customer's country code
     * @return String containing the customer's country code
     */
    public String getCountryCode() {
        return phone.getCountryCode();
    }

    /**
     * Getter for the customer's phone number
     * @return String containing the customer's phone number
     */
    public String getNumber() {
        return phone.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustDetails that = (CustDetails) o;

        if (address1 != null ? !address1.equals(that.address1) : that.address1 != null)
            return false;
        if (address2 != null ? !address2.equals(that.address2) : that.address2 != null)
            return false;
        if (postCode != null ? !postCode.equals(that.postCode) : that.postCode != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        return !(phone != null ? !phone.equals(that.phone) : that.phone != null);

    }


    /**
     * Class used to represent one's phone information
     */
    class Phone {
        private String countryCode;
        private String number;

        /**
         * Default constructor. The Phone object should be instantiated via the CustDetails constructor
         * @param countryCode String containing the customer's country code
         * @param number String containing the customer's phone number
         */
        public Phone(String countryCode, String number) {
            this.number = number;
            this.countryCode = countryCode;
        }

        /**
         * Getter for the customer's country code
         * @return String containing the customer's country code
         */
        public String getCountryCode() {
            return countryCode;
        }

        /**
         * Getter for the customer's phone number
         * @return String containing the customer's phone number
         */
        public String getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Phone phone = (Phone) o;

            if (countryCode != null ? !countryCode.equals(phone.countryCode) : phone.countryCode != null)
                return false;
            return !(number != null ? !number.equals(phone.number) : phone.number != null);

        }
    }
}