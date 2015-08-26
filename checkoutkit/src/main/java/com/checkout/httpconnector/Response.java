package com.checkout.httpconnector;

/**
 * Class used to modelise a Response from the server, containing the actual data returned, in model and all the information related to the response
 *
 * Created by manonh on 07/08/2015.
 */
 public class Response<T> {

        public boolean hasError = false;
        public int httpStatus;
        public ResponseError error;
        public T model;

    /**
     * Default constructor
      * @param model : Class of the object returned by the server
     */
        public Response(T model) {
            this.model = model;
        }
    }

