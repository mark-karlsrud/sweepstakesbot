package com.markkarlsrud.sweepstakesbot.client;

import com.google.common.collect.Lists;
import com.markkarlsrud.sweepstakesbot.Bot;
import com.markkarlsrud.sweepstakesbot.RedditBot;
import com.markkarlsrud.sweepstakesbot.listener.MyStatusListener;
import com.markkarlsrud.sweepstakesbot.model.TwitterCredentials;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mkarlsru on 6/26/17.
 */
public class StreamClient {
    private static final int NUM_THREADS = 1;

    private final Client client;
    private final Twitter4jStatusClient t4jClient;

    public StreamClient(TwitterCredentials creds, List<String> filterTerms) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(filterTerms);
        Authentication auth = new OAuth1(creds.getConsumerKey(), creds.getConsumerSecret(), creds.getToken(), creds.getSecret());

        client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
        // calling the listeners on each message

        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);

        TwitterFactory factory = new TwitterFactory();

        //This instance is threadsafe, so it can be added to multiple listeners
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(creds.getConsumerKey(), creds.getConsumerSecret());
        AccessToken accessToken = new AccessToken(creds.getToken(), creds.getSecret());
        twitter.setOAuthAccessToken(accessToken);

        Bot bot = new Bot(twitter, new RedditBot());

        MyStatusListener listener = new MyStatusListener(bot);
        // Wrap our BasicClient with the twitter4j client
        t4jClient = new Twitter4jStatusClient(client, queue, Lists.newArrayList(listener), service);

        // Establish a connection
        t4jClient.connect();

    }

    public void run() throws InterruptedException {
        for (int threads = 0; threads < NUM_THREADS; threads++) {
            // This must be called once per processing thread
            t4jClient.process();
        }

//        Thread.sleep(10000);

//        client.stop();

        // Establish a connection
//        client.connect();
    }
}
