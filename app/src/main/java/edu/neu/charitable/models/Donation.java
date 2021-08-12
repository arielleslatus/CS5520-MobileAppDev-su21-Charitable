package edu.neu.charitable.models;

import java.sql.Timestamp;

public class Donation {
    public long timestamp;
    public String charity;
    public String user;
    public float amount;

    public Donation(){
    }

    public Donation(long ts,  String charity, String user, float amount) {
        this.timestamp = ts;
        this.charity = charity;
        this.user = user;
        this.amount = amount;
    }

    public Donation(String charity, String user, float amount) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.timestamp = timestamp.getTime();
        this.charity = charity;
        this.user = user;
        this.amount = amount;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCharity() {
        return charity;
    }

    public void setCharity(String charity) {
        this.charity = charity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
