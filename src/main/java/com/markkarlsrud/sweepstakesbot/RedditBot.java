package com.markkarlsrud.sweepstakesbot;

import com.google.gson.Gson;
import com.markkarlsrud.sweepstakesbot.model.RedditAuthResponse;
import com.markkarlsrud.sweepstakesbot.model.reddit.Child;
import com.markkarlsrud.sweepstakesbot.model.reddit.Subreddit;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class RedditBot {
    private final HttpClient client;
    private final Gson gson;

    private String accessToken;
    
    public RedditBot() {
        this.gson = new Gson();
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(new AuthScope("ssl.reddit.com", 443),
//                new UsernamePasswordCredentials("Dn8qJUWtyuNefQ", "F9Zqj2ABUJG6phdPgpuxZKPsZv0"));
        this.client = HttpClientBuilder.create()
//                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
    
    public String getAccessToken() throws IOException {
        HttpPost request = new HttpPost("https://ssl.reddit.com/api/v1/access_token");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("redirect_uri", "https://www.google.com/"));

        request.setEntity(new UrlEncodedFormEntity(nvps));
        request.addHeader("User-Agent", "d2671b48-a2b9-4ff7-8db1-7bf5cc10eb5c");
        request.setHeader("Accept","application/json");

        // System.out.println("executing request " + httppost.getRequestLine());

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();

        //System.out.println(response.getStatusLine());
        if (entity != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder content = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
                content.append(line);
            }
            RedditAuthResponse authResponse = gson.fromJson(content.toString(), RedditAuthResponse.class);
            accessToken = authResponse.getAccessToken();
            System.out.println(authResponse.getAccessToken());
            return authResponse.getAccessToken();
        }
        return null;
    }

    public String getRandomComment() throws IOException {
        HttpGet request = new HttpGet("https://www.reddit.com/r/random/.json?limit=20");
        request.addHeader("User-Agent", "d2671b48-a2b9-4ff7-8db1-7bf5cc10eb5c");
        request.setHeader("Accept","application/json");

        HttpResponse response = client.execute(request);
        String responseStr = getResponse(response);
        if (responseStr != null) {
            Subreddit subreddit = gson.fromJson(responseStr, Subreddit.class);
            for (Child child : subreddit.getData().getChildren()) {
                if (child.getData().getNumComments() > 0) {
                    return getComment(child.getData().getId());
                }
            }
        }

        return null;
    }

    private String getComment(String pageId) throws IOException {
        HttpGet request = new HttpGet("https://www.reddit.com/" + pageId + "/.json?limit=1");
        request.addHeader("User-Agent", "d2671b48-a2b9-4ff7-8db1-7bf5cc10eb5c");
        request.setHeader("Accept","application/json");

        HttpResponse response = client.execute(request);
        String responseStr = getResponse(response);
        if (responseStr != null) {
            Subreddit[] listings = gson.fromJson(responseStr, Subreddit[].class);
            for (Subreddit listing : listings) {
                if (listing.getData().getChildren()[0].getData().getBody() != null) {
                    return listing.getData().getChildren()[0].getData().getBody();
                }
            }
        }

        return null;
    }

    private String getResponse(HttpResponse response) throws IOException {
        if (response.getEntity() != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder content = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
                content.append(line);
            }
            return content.toString();
        }
        return null;
    }

}
