package com.checkout.httpconnector;

import com.checkout.logger.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class used to manage the http connections with Checkout's server. Serves as an abstraction for get and post requests
 */
public class HttpConnector {

    /**
     * Enumeration representing the different types of HTTP methods that can be used
     */
    public enum HttpMethods { GET, POST }

    private Gson gson;
    private HttpURLConnection connection = null;
    private int httpStatus = 0;
    public static Log logger=null;
    private boolean debug;

    /**
     * Default constructor
     * @param gsonInstance gson object used for JSON parsing
     * @param debug Boolean if we need to log activity or not
     * @param logger Log object, where to log if debug is true
     */
    public HttpConnector(Gson gsonInstance, boolean debug, Log logger){
        this.debug = debug;
        gson = gsonInstance;
        HttpConnector.logger = logger;
    }

    /**
     * Setter for the debug boolean
     * @param debug Boolean containing the new value for debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /*
     * Private method handling the sending and receiving of the HTTP requests
     * @param uri String containing the url the request is sent to
     * @param apiKey String containing the public key of the merchant
     * @param method String containing the HTTP method
     * @param payload String containing the payload to be sent (for a POST request)
     * @param returnType T instance returned by the server
     * @param <T>
     * @return Response<T>, Response object containing a T instance corresponding to the server's response
     * @throws IOException If there is a problem with the communication with the server
     * @throws JsonSyntaxException If the JSON is not correct, it cannot be parsed
     */
    private <T> Response<T> sendRequest(String uri,String apiKey,HttpMethods method, String payload,Class<T> returnType) throws IOException,JsonSyntaxException {
        com.checkout.httpconnector.Response<T> response = null;
        JsonObject json = null;
        String lines = null;
        T jsonObject = null;
        BufferedReader reader = null;
        OutputStreamWriter outputStreamWriter=null;

        URL url = new URL(uri);

        if(debug){
            logger.info("**Request**  	"+method+":	"+uri);
            logger.info("**Payload**	"+payload);
        }

        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toString());
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setDoOutput(true);

            connection.connect();

            if(HttpMethods.POST == method){
                outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                outputStreamWriter.write(payload);
                outputStreamWriter.flush();
            }

            httpStatus = connection.getResponseCode();

            if (this.httpStatus == 200) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                lines = reader.readLine();

                if(lines!=null){
                    json = gson.fromJson(lines, JsonObject.class);
                }

                if(debug){
                    logger.info("** HttpResponse**  Status 200 OK"+json);
                }

                jsonObject = gson.fromJson(json.toString(),returnType);

                response = new com.checkout.httpconnector.Response<T>(jsonObject);
                response.httpStatus= this.httpStatus;
            } else{

                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                lines = reader.readLine();

                if(lines!=null){
                    json = gson.fromJson(lines, JsonObject.class);
                }

                ResponseError error = gson.fromJson(json.toString(),ResponseError.class);

                response = new com.checkout.httpconnector.Response<T>(jsonObject);

                response.error=error;
                response.hasError=true;
                response.httpStatus= this.httpStatus;

                if(debug){
                    logger.info("** HttpResponse**  StatusError: "+response.httpStatus+json);
                }
            }

            reader.close();

            return response;

        }catch (IOException e) {
            if(debug){
                logger.info("** Exception ** "+ e.toString());
            }
            throw e;
        }
        finally {

            if(reader!=null){
                reader.close();
            }

            if(outputStreamWriter!=null){
                outputStreamWriter.close();
            }

            if(connection!=null){
                connection.disconnect();
            }
        }
    }


    /**
     * Method allowing to send a POST request to a given url with a payload
     * @param url String containing the url the request must be sent to
     * @param key String containing the public key of the merchant
     * @param payload String containing the data to be sent with the request
     * @param returnType T instance returned by the server
     * @param <T>
     * @return Response<T>, Response object containing a T instance corresponding to the server's response
     * @throws IOException If there is a problem with the communication with the server
     * @throws JsonSyntaxException If the JSON is not correct, it cannot be parsed
     */
    public <T> Response<T> postRequest(String url,String key,String payload,Class<T> returnType) throws JsonSyntaxException, IOException {

        return sendRequest(url,key, HttpMethods.POST, payload,returnType);
    }

    /**
     * Method allowing to send a GET request to a given url
     * @param url String containing the url the request must be sent to
     * @param key String containing the public key of the merchant
     * @param returnType T instance returned by the server
     * @param <T>
     * @return Response<T>, Response object containing a T instance corresponding to the server's response
     * @throws IOException If there is a problem with the communication with the server
     * @throws JsonSyntaxException If the JSON is not correct, it cannot be parsed
     */
    public <T> Response<T> getRequest(String url,String key,Class<T> returnType) throws JsonSyntaxException, IOException{

        return sendRequest(url,key, HttpMethods.GET, null,returnType);
    }
}
