package com.markkarlsrud.sweepstakesbot.model.reddit;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class Child {
    private final String kind;
    private final ChildData data;

    public Child(String kind, ChildData data) {
        this.kind = kind;
        this.data = data;
    }

    public String getKind() {
        return kind;
    }

    public ChildData getData() {
        return data;
    }
}
