/* 
* @File: HttpClient.java
* @Author: Abhi Gupta
* 
* This class handler all of the web traffic between the client and the server. Users can use this class
* to make HTTP Post and Get requests to an API or their own endpoint with as many paramaters as needed
*/

package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


public class HttpClient implements Database {
    private static HttpPost sendMessage;    // each preconfigured with its respective headers though it is not neccessary
    private static HttpPost signUp;
    private static HttpPost aiMessage;

    static {
        // Database
        sendMessage = new HttpPost(BASE_URL + FUNCTION_SEND_MESSAGE);
        sendMessage.setHeader("X-Parse-Application-Id", PARSE_APP_ID);
        sendMessage.setHeader("X-Parse-REST-API-Key", PARSE_REST_API_KEY);
        sendMessage.setHeader("Content-Type", "application/json");

        signUp = new HttpPost(BASE_URL + FUNCTION_SIGNUP);
        signUp.setHeader("X-Parse-Application-Id", PARSE_APP_ID);
        signUp.setHeader("X-Parse-REST-API-Key", PARSE_REST_API_KEY);
        signUp.setHeader("Content-Type", "application/json");

        // AI
        aiMessage = new HttpPost("https://api.api.ai/v1/query?v=20150910");
        aiMessage.setHeader("Content-Type", "application/json");
        aiMessage.setHeader("Authorization", "Bearer 97489efe85074626bf652c0c349d21ec");

    }

    /**
    * Sends a message to a recipient existing in the database by making a HTTP request to custom backend methods written in javascript
    */
    public static String sendMessage(Map<String, String> params) {
        String paramsURL = "{";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            paramsURL += String.format("\"%s\":\"%s\",", key, value);
        }
        paramsURL = paramsURL.substring(0, paramsURL.length() - 1) + "}";
        return postRequest(FUNCTION_SEND_MESSAGE, paramsURL);   // FUNCTION_SEND_MESSAGE is the web endpoint for the method sendMessage
    }

    /**
    * Sends a HTTP request to the AI API with a uniquely configured model that has trained the AI for this app's purpose
    */
    public static String aiMessage(Map<String, String> params) {
        String paramsURL = "{";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            paramsURL += String.format("\"%s\":\"%s\",", key, value);
        }
        paramsURL = paramsURL.substring(0, paramsURL.length() - 1) + "}";
        return postRequest(paramsURL);
    }

    /**
    * Tells the database to create a new user with the specified parameters by making a HTTP request to custom backend methods written in javascript
    */
    public static boolean signUpUser(Map<String, String> params) {
        String paramsURL = "{";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            paramsURL += String.format("\"%s\":\"%s\",", key, value);
        }
        paramsURL = paramsURL.substring(0, paramsURL.length() - 1) + "}";
        return postSignUpRequest(FUNCTION_SIGNUP, paramsURL);
    }


    // A generic POST request for web endpoints
    private static String postRequest(String function, String params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost request;

            StringEntity input = new StringEntity(params);
            input.setContentType("application/json");
            if (function.equals(FUNCTION_SEND_MESSAGE)) {
                request = sendMessage;
                return getResponse(httpClient, input, request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // A generic POST request for APIs
    private static String postRequest(String params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost request;
            StringEntity input = new StringEntity(params);
            input.setContentType("application/json");
            request = aiMessage;
            JSONObject resp = getResponseJSON(httpClient, input, request);
            return resp.getJSONObject("result").getJSONObject("fulfillment").getString("speech");    // the AI's reply to a user's question

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // A specific POST request for the signUp web endpoint
    private static boolean postSignUpRequest(String function, String params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost request;

            StringEntity input = new StringEntity(params);
            input.setContentType("application/json");
            if (function.equals(FUNCTION_SIGNUP)) {
                request = signUp;
                return getSignUpResponse(httpClient, input, request);
            }
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // A generic POST response for all HTTP Post requests
    private static String getResponse(DefaultHttpClient httpClient, StringEntity input, HttpPost request) {
        try {
            if (request != null) {
                request.setEntity(input);
                HttpResponse response = httpClient.execute(request);

                BufferedReader outputReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;

                System.out.print("Output from Server .... ");
                while ((output = outputReader.readLine()) != null) {
                }
                httpClient.getConnectionManager().shutdown();
                return "Http request was successfully made.";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Http request failed.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Http request failed.";
        }
        return null;
    }

    // A specific POST response for the AI's reply to a user's message (encoded in JSON for ease of access)
    private static JSONObject getResponseJSON(DefaultHttpClient httpClient, StringEntity input, HttpPost request) {
        try {
            if (request != null) {
                request.setEntity(input);
                HttpResponse response = httpClient.execute(request);

                BufferedReader outputReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;
                String results = "";
                System.out.print("Output from Server .... ");
                while ((output = outputReader.readLine()) != null) {
                    results += output;
                }
                httpClient.getConnectionManager().shutdown();
                return new JSONObject(results);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    // A specific POST response for asking the server to create a new user - notifies the client if the request succeeded or failed
    private static boolean getSignUpResponse(DefaultHttpClient httpClient, StringEntity input, HttpPost request) {
        try {
            if (request != null) {
                request.setEntity(input);
                HttpResponse response = httpClient.execute(request);

                BufferedReader outputReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                String output;

                if (response.getStatusLine().getStatusCode() == 400) {
                    return false;
                }
                System.out.print("Output from Server .... ");
                while ((output = outputReader.readLine()) != null) {
                    System.out.println(output);
                }
                httpClient.getConnectionManager().shutdown();
                return true;
            }
        } catch (MalformedURLException e) {
            System.out.println("error");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("error2");
            e.printStackTrace();
            return false;
        }
        return false;
    }
}