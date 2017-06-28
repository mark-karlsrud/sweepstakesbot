package com.markkarlsrud.sweepstakesbot;

import com.twitter.hbc.httpclient.ControlStreamException;
import twitter4j.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class Bot {
    private static final int BREAK_TIME_IN_SECONDS = 15;
    private static final int POPULAR_THRESHOLD = 20;
    private static final int RECENT_THRESHOLD_IN_DAYS = 10;
    private static final int ODDS_OF_OWN_TWEET = 5;

    private final Twitter twitter;
    private final RedditBot redditBot;
    private final Random random;

    /*
    This list stores retweets, so at the top level is the bot's info. Use status.getRetweetedStatus() to get the original tweet.
     */
    private final List<Status> myRetweets;

    public Bot(Twitter twitter, RedditBot redditBot) {
        this.twitter = twitter;
        this.redditBot = redditBot;
        this.random = new Random();
        this.myRetweets = new ArrayList<>();
        loadMyRetweets();
    }

    public void processTweet(Status tweet) {
        tweet = getSourceTweet(tweet);
        if (satisfiesConditions(tweet)) {
            try {
                followUser(tweet);
                likeTweet(tweet);
                retweet(tweet);

                //Every successful retweet, think about posting a status of our own
                if (random.nextInt(ODDS_OF_OWN_TWEET) == 0) {
                    try {
                        postTweet(redditBot.getRandomComment());
                    } catch (TwitterException | IOException e) {
                        e.printStackTrace();
                    }
                }

                takeBreak();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load bot's current retweets, based on the time the bot retweeted it to ensure that we don't retweet a tweet
     * that we've already retweeted
     */
    private void loadMyRetweets() {
        try {
            ResponseList<Status> responseList;
            int i = 1;
            do {
                twitter.getUserTimeline(new Paging());
                responseList = twitter.getUserTimeline(new Paging(i ++));
                responseList.removeIf(tweet -> !tweet.isRetweeted());
                myRetweets.addAll(responseList);
            } while (isRecentTweet(responseList.get(0)));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        //TODO ensure sorted by time
        System.out.println("loaded retweets");
    }

    /**
     * If a tweet is a retweet, get the source tweet recursively
     */
    private Status getSourceTweet(Status tweet) {
        if (tweet.getRetweetedStatus() != null) {
            return getSourceTweet(tweet.getRetweetedStatus());
        }
        return tweet;
    }

    private boolean satisfiesConditions(Status tweet) {
        return isPopular(tweet) && isRecentTweet(tweet)  && hasKeyTerms(tweet)
                //This is the most expensive operation, so keep last
                && !retweetedAlready(tweet);
    }

    private boolean isPopular(Status tweet) {
        return tweet.getRetweetCount() >= POPULAR_THRESHOLD;
    }

    private boolean isRecentTweet(Status tweet) {
        Calendar threeDaysAgo = Calendar.getInstance();
        threeDaysAgo.add(Calendar.DAY_OF_WEEK, -1 * RECENT_THRESHOLD_IN_DAYS);
        return threeDaysAgo.getTimeInMillis() < tweet.getCreatedAt().getTime();
    }

    private boolean hasKeyTerms(Status tweet) {
        String status = tweet.getText().toLowerCase();
        return (status.contains("rt ") || status.contains("retweet")) &&
                (status.contains("could") || status.contains("chance")) &&
                (status.contains("win") || status.contains("giveaway"));
    }

    private void followUser(Status tweet) throws IOException, ControlStreamException, TwitterException {
        if (tweet.getText().toLowerCase().contains("follow") &&
                !twitter.showFriendship(twitter.getId(), tweet.getUser().getId()).isSourceFollowingTarget()) {
            System.out.println("following user " + tweet.getUser().getScreenName());
            twitter.createFriendship(tweet.getUser().getId());
        }
    }

    private void likeTweet(Status tweet) throws TwitterException {
        if (tweet.getText().toLowerCase().contains("like")) {
            System.out.println("liking tweet " + tweet.getId());
            twitter.createFavorite(tweet.getId());
        }
    }

    private void retweet(Status tweet) throws TwitterException {
        System.out.println("retweeting: " + tweet.getText());
        Status retweet = twitter.retweetStatus(tweet.getId());
        myRetweets.add(retweet);
    }

    private boolean retweetedAlready(Status tweet) {
        for (Status retweet : myRetweets) {
            if (retweet.getRetweetedStatus().getId() == tweet.getId()) {
                return true;
            }
        }
        return false;
    }

    private void takeBreak() throws InterruptedException {
        cleanMyRetweets();
        Thread.sleep(TimeUnit.SECONDS.toMillis(BREAK_TIME_IN_SECONDS));
    }

    /**
     * Ensure that the retweet list doesn't get too large.
     * TODO: optimize by keeping myRetweet list sorted by time, so that we break this loop when we reach a "current" timestamp
     */
    private void cleanMyRetweets() {
        myRetweets.removeIf(retweet -> !isRecentTweet(retweet));
    }

    public void removeTweetFromMemory(StatusDeletionNotice statusDeletionNotice) {//        myRetweets.removeIf(status -> { status.getId() == statusDeletionNotice.getStatusId()});
        ListIterator<Status> iterator = myRetweets.listIterator();
        while (iterator.hasNext()) {
            Status status = iterator.next().getRetweetedStatus();
            if (status.getId() == statusDeletionNotice.getStatusId()) {
                iterator.remove();
                return;
            }
        }
    }

    public void postTweet(String text) throws TwitterException {
        if (text.length() > 140) {
            text = text.substring(0, 139);
        }
        twitter.updateStatus(text);
    }
}
