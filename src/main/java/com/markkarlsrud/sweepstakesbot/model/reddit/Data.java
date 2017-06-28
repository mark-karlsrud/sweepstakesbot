package com.markkarlsrud.sweepstakesbot.model.reddit;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class Data {
    private final String modhash;
    private final Child[] children;
    private final String after;
    private final String before;

    public Data(String modhash, Child[] children, String after, String before) {
        this.modhash = modhash;
        this.children = children;
        this.after = after;
        this.before = before;
    }

    public String getModhash() {
        return modhash;
    }

    public Child[] getChildren() {
        return children;
    }

    public String getAfter() {
        return after;
    }

    public String getBefore() {
        return before;
    }
}
