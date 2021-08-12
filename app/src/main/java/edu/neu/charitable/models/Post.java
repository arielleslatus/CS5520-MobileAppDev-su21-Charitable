package edu.neu.charitable.models;


import java.sql.Timestamp;

/**
 * Simplest solution I could think of to work with recycler view and without additional querying/casting.
 * All posts stored as same object type, but can be inflated to different views.
 */
public class Post {
    public long timestamp;
    public String type; // donation, match, goal_complete, charity_help, charity_project
    public String user;
    public String charity;
    public String matchedUser;
    public float amount;
    public String text;
    public int numApplauds;

    public Post() {
    }

    public Post(long timestamp, String type, String user, String charity, String matchedUser, float amount, String text, int numApplauds) {
        this.timestamp = timestamp;
        this.type = type;
        this.user = user;
        this.charity = charity;
        this.matchedUser = matchedUser;
        this.amount = amount;
        this.text = text;
        this.numApplauds = numApplauds;
    }

    //creates a post with current time
    public Post(String type, String user, String charity, String matchedUser, float amount, String text, int numApplauds) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.timestamp = timestamp.getTime();
        this.type = type;
        this.user = user;
        this.charity = charity;
        this.matchedUser = matchedUser;
        this.amount = amount;
        this.text = text;
        this.numApplauds = numApplauds;
    }


}
