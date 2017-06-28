package com.markkarlsrud.sweepstakesbot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class RedditAuthResponse {
    @SerializedName("access_token")
    private final String accessToken;
    @SerializedName("token_type")
    private final String tokenType;
    @SerializedName("expires_in")
    private final int expiresIn;
    private final String scope;

    public RedditAuthResponse(String accessToken, String tokenType, int expiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }
}
