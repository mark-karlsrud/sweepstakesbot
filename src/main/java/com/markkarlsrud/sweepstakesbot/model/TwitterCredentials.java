package com.markkarlsrud.sweepstakesbot.model;

/**
 * Created by mkarlsru on 6/25/17.
 */
public class TwitterCredentials {
    private final String consumerKey;
    private final String consumerSecret;
    private final String token;
    private final String secret;

    public TwitterCredentials(String consumerKey, String consumerSecret, String token, String secret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.secret = secret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }
}
