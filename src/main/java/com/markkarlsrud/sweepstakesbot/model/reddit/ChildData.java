package com.markkarlsrud.sweepstakesbot.model.reddit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mkarlsru on 6/27/17.
 */
public class ChildData {
    private final String id;
    private final String title;
    @SerializedName("num_comments")
    private final int numComments;
    private final String body;

    public ChildData(String id, String title, int numComments, String body) {
        this.id = id;
        this.title = title;
        this.numComments = numComments;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getNumComments() {
        return numComments;
    }

    public String getBody() {
        return body;
    }
}
