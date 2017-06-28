package com.markkarlsrud.sweepstakesbot.listener;

import com.markkarlsrud.sweepstakesbot.Bot;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by mkarlsru on 6/26/17.
 *
 * Methods of this class are called from the Twitter4jSitestreamClient
 *
 * StatusListener documentation: http://twitter4j.org/javadoc/twitter4j/StatusListener.html#onStallWarning-twitter4j.StallWarning-
 */
public class MyStatusListener implements StatusListener {
    private final Bot bot;

    public MyStatusListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onStatus(Status tweet) {
        bot.processTweet(tweet);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        bot.removeTweetFromMemory(statusDeletionNotice);
    }

    @Override
    public void onTrackLimitationNotice(int i) {

    }

    @Override
    public void onScrubGeo(long l, long l1) {
        //TODO remove geo information from tweet in memory
    }

    /**
     * This happens at a max of once every five minutes, but we shouldn't get this warning because of our low-bandwidth bot
     * @param stallWarning StallWarning
     */
    @Override
    public void onStallWarning(StallWarning stallWarning) {
        System.err.println(stallWarning);
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sleep for a minute
     * @param e Exception
     */
    @Override
    public void onException(Exception e) {
        e.printStackTrace();
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
