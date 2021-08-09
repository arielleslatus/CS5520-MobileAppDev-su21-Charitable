package edu.neu.charitable.models;

import java.sql.Timestamp;

public class Goal {
    public long start;
    public String charity;
    public float amountSet;
    public float amoundDonated;
    public boolean complete;

    public Goal () {
    }

    public Goal (String charity, float amountSet, float amoundDonated) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.start = timestamp.getTime();
        this.charity = charity;
        this.amoundDonated = amoundDonated;
        this.amountSet = amountSet;
        complete = false;
    }

    public Goal (long start, String charity, float amountSet, float amoundDonated, boolean complete) {
        this.start = start;
        this.charity = charity;
        this.amoundDonated = amoundDonated;
        this.amountSet = amountSet;
        this.complete = false;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getCharity() {
        return charity;
    }

    public void setCharity(String charity) {
        this.charity = charity;
    }

    public float getAmountSet() {
        return amountSet;
    }

    public void setAmountSet(float amountSet) {
        this.amountSet = amountSet;
    }

    public float getAmoundDonated() {
        return amoundDonated;
    }

    public void setAmoundDonated(float amoundDonated) {
        this.amoundDonated = amoundDonated;
    }
}
