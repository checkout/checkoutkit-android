package com.checkout;

import com.checkout.exceptions.CheckoutException;
import com.checkout.httpconnector.HttpConnector;
import com.checkout.httpconnector.HttpConnector.HttpMethods;
import com.checkout.httpconnector.Response;
import com.checkout.logger.Log;
import com.checkout.models.Card;
import com.checkout.models.CardProviderResponse;
import com.checkout.models.CardTokenResponse;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.SocketHandler;
import java.util.regex.Pattern;


/**
 * Main class allowing to create one CheckoutKit instance, provide the merchant's public key and create card tokens
 */
public class CheckoutKit {

    /*
     * Enumeration containing the basic definition of the available REST functions
     */
    private enum RESTFunctions {
        GETCARDPROVIDERS("providers/cards", HttpMethods.GET),
        CREATECARDTOKEN("tokens/card", HttpMethods.POST);

        private String url;
        private HttpMethods method;

        RESTFunctions(String url, HttpMethods method) {
            this.url = url;
            this.method = method;
        }
    }

    /**
     * Enum containing the different environments for generating the tokens, it contains the URL corresponding to the environment
     */
    public enum Environment {
        SANDBOX("http://sandbox.checkout.com/api2/v2/"), LIVE("https://api2.checkout.com/v2/");

        private String url;

        Environment(String url) {
            this.url = url;
        }
    }


    private static CheckoutKit instance = null; /* Used for singleton pattern */
    private static String PUBLIC_KEY_REGEX_VALIDATION = "^pk_(?:test_)?(?:\\w{8})-(?:\\w{4})-(?:\\w{4})-(?:\\w{4})-(?:\\w{12})$";
    /* Used for testing purposes, overrides the value of the Environment URL (replaced by localhost in unit tests) */
    protected String baseUrlOverride;
    private String publicKey;
    private Environment baseUrl;
    private boolean logging;
    private Log logger;

    protected HttpConnector httpClient;
    protected Gson gson;


    /*
     * Private constructor used for the Singleton Pattern
     *
     * @param publicKey String containing the merchant's public key
     * @param baseUrl   Environment object containing the information of the merchant's environment, default is SANDBOX
     * @param debug   boolean, if the debug mode is activated or not, default is true
     * @param logger    Log instance for logging purposes if debug mode is activated
     */
    private CheckoutKit(String publicKey, Environment baseUrl, boolean debug, Log log) {
        this.publicKey = publicKey;
        this.baseUrl = baseUrl;
        this.logging = debug;
        gson = new Gson();
        this.logger = log;
        httpClient = new HttpConnector(gson, debug, logger);
        if(logging){
            logger.info("**CheckoutKit created**  	"+ publicKey);
        }

    }

    /**
     * Sets the logger
     * @param logger Log object
     */
    public void setLogger(Log logger) {
        this.logger = logger;
    }

    /**
     * Getter for the logger
     * @return current Log object
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, Environment.SANDBOX, true, Log.getLog());
        } else {
            instance.setPublicKey(publicKey);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param baseUrl   Environment object containing the desired environment (overwrites SANDBOX as the default one)
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, Environment baseUrl) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, baseUrl, true, Log.getLog());
        } else {
            instance.setPublicKey(publicKey);
            instance.setEnvironment(baseUrl);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param debug   boolean containing the information about debug mode (overwrites true as the default mode)
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, boolean debug) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, Environment.SANDBOX, debug, Log.getLog());
        } else {
            instance.setPublicKey(publicKey);
            instance.setDebugMode(debug);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param baseUrl   Environment object containing the desired environment (overwrites SANDBOX as the default one)
     * @param debug   boolean containing the information about debug mode (overwrites true as the default mode)
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, Environment baseUrl, boolean debug) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, baseUrl, debug, Log.getLog());
        } else {
            instance.setPublicKey(publicKey);
            instance.setEnvironment(baseUrl);
            instance.setDebugMode(debug);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param debug   boolean containing the information about debug mode (overwrites true as the default mode)
     * @param logger    Log instance for logging purposes if debug mode is activated
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, boolean debug, Log logger) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, Environment.SANDBOX, debug, logger);
        } else {
            instance.setPublicKey(publicKey);
            instance.setLogger(logger);
            instance.setDebugMode(debug);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param baseUrl   Environment object containing the desired environment (overwrites SANDBOX as the default one)
     * @param debug   boolean containing the information about debug mode (overwrites true as the default mode)
     * @param logger    Log instance for logging purposes if debug mode is activated
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, Environment baseUrl, boolean debug, Log logger) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey)) instance = new CheckoutKit(publicKey, baseUrl, debug, logger);
        } else {
            instance.setPublicKey(publicKey);
            instance.setEnvironment(baseUrl);
            instance.setDebugMode(debug);
            instance.setLogger(logger);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param baseUrl   Environment object containing the desired environment (overwrites SANDBOX as the default one)
     * @param logger    Log instance for logging purposes if debug mode is activated
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, Environment baseUrl, Log logger) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey)) instance = new CheckoutKit(publicKey, baseUrl, true, logger);
        } else {
            instance.setPublicKey(publicKey);
            instance.setEnvironment(baseUrl);
            instance.setLogger(logger);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance
     *
     * @param publicKey String containing the merchant's public key
     * @param logger    Log instance for logging purposes if debug mode is activated
     * @return a unique CheckoutKit instance
     * @throws CheckoutException if the public key is not valid
     */
    public static CheckoutKit getInstance(String publicKey, Log logger) throws CheckoutException {
        if (instance == null) {
            if (checkPK(publicKey))
                instance = new CheckoutKit(publicKey, Environment.SANDBOX, true, logger);
        } else {
            instance.setPublicKey(publicKey);
            instance.setLogger(logger);
        }
        return instance;
    }

    /**
     * Function used for the Singleton Pattern, returns a unique CheckoutKit instance, to be used once the CheckoutKit object has been instantiated to retrieve it
     *
     * @return null if getInstance has not been called before specifying all the parameters or the CheckoutKit object
     * @throws CheckoutException if the CheckoutKit object has not been instantiated before
     */
    public static CheckoutKit getInstance() throws CheckoutException {
        if (instance == null)
            throw new CheckoutException(CheckoutException.CKExceptionType.NO_PUBLIC_KEY);
        return instance;
    }

    /**
     * Small utility function used for testing purposes, sets the instance to null so that you can create another one with resetted parameters
     */
    public static void destroy() {
        instance = null;
    }

    /*
     * Function used to check if the merchant's public key is valid
     *
     * @param pk String containing the public key to be tested
     * @return boolean, if the public key is valid or not
     * @throws CheckoutException if the public key is not valid
     */
    private static boolean checkPK(String pk) throws CheckoutException {
        if (!Pattern.matches(PUBLIC_KEY_REGEX_VALIDATION, pk))
            throw new CheckoutException(CheckoutException.CKExceptionType.INVALID_PUBLIC_KEY);
        else return true;
    }

    /**
     * Getter for the url of the environment
     *
     * @return String containing the url of the environment
     */
    public String getEnvironmentUrl() {
        return baseUrl.url;
    }

    /**
     * Getter for the complete url, with the wanted
     *
     * @param f RESTFunctions object containing the called function
     * @return String containing the complete url to make the call to the server for the function f
     */
    public String getUrl(RESTFunctions f) {
        return (baseUrlOverride == null ? this.baseUrl.url : baseUrlOverride) + f.url;
    }

    /**
     * Getter for the debug mode
     *
     * @return boolean containing the value of the debug mode
     */
    public boolean getDebugMode() {
        return this.logging;
    }

    /**
     * Changes the value of the debug mode
     *
     * @param debug boolean containing the new value of the debug mode
     */
    public void setDebugMode(boolean debug) {
        this.logging = debug;
        this.httpClient.setDebug(debug);
    }

    /**
     * Getter for the environment
     *
     * @return current Environment object
     */
    public Environment getEnvironment() {
        return this.baseUrl;
    }

    /**
     * Changes the environment
     *
     * @param env Environment object containing the new environment
     */
    public void setEnvironment(Environment env) {
        this.baseUrl = env;
    }

    /**
     * Getter for the public key
     *
     * @return String containing the public key
     */
    public String getPublicKey() {
        return this.publicKey;
    }

    /**
     * Changes the value of the public key if the new one is valid
     *
     * @param publicKey String containing the new public key
     * @throws CheckoutException if the public key is not valid
     */
    public void setPublicKey(String publicKey) throws CheckoutException {
        if (checkPK(publicKey)) this.publicKey = publicKey;
    }

    /**
     * Function that calls getCardProviders via REST on the server specified in Environment
     *
     * @return CardProvider array containing the Checkout card providers
     * @throws CheckoutException if any problem occurred in the communication with the server or if the public key is not valid
     */
    public Response<CardProviderResponse> getCardProviders() throws CheckoutException, IOException {
        if(logging){
            logger.info("**GetCardProviders called**  	" + publicKey);
        }
        return httpClient.getRequest(getUrl(RESTFunctions.GETCARDPROVIDERS), publicKey, CardProviderResponse.class);
    }

    /**
     * Function that calls createCardToken via REST on the server specified in Environment
     *
     * @param card Card object containing the informations to be tokenized
     * @return CardToken object containing all the information received by the server
     * @throws CheckoutException if any problem occurred in the communication with the server or if the public key is not valid
     */
    public Response<CardTokenResponse> createCardToken(Card card) throws CheckoutException, IOException {
        if(logging){
            logger.info("**CreateCardToken called**  	"+ publicKey);
        }
        return httpClient.postRequest(getUrl(RESTFunctions.CREATECARDTOKEN), publicKey, gson.toJson(card), CardTokenResponse.class);
    }
}