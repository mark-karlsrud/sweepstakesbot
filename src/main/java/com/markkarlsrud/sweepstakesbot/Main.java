package com.markkarlsrud.sweepstakesbot;

import com.markkarlsrud.sweepstakesbot.client.StreamClient;
import com.markkarlsrud.sweepstakesbot.config.PropertiesManager;
import com.markkarlsrud.sweepstakesbot.model.TwitterCredentials;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by mkarlsru on 6/25/17.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        TwitterCredentials creds = getCreds();
        run(creds, PropertiesManager.getCSVStrings("Keywords"));
    }

    private static void run(TwitterCredentials creds, List<String> filterTerms) throws InterruptedException {
        StreamClient client = new StreamClient(creds, filterTerms);
        client.run();
    }

    private static TwitterCredentials getCreds() throws IOException {
        Properties properties = PropertiesManager.getProperties("TwitterKeys");
        return new TwitterCredentials(properties.getProperty("CONSUMER_KEY"), properties.getProperty("CONSUMER_SECRET"),
                properties.getProperty("ACCESS_TOKEN"), properties.getProperty("ACCESS_TOKEN_SECRET"));
    }
}
