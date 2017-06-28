package com.markkarlsrud.sweepstakesbot.model.reddit;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class Subreddit {
    private final String kind;
    private final Data data;

    public Subreddit(String kind, Data data) {
        this.kind = kind;
        this.data = data;
    }

    public String getKind() {
        return kind;
    }

    public Data getData() {
        return data;
    }
}
