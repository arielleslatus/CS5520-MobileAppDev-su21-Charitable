package edu.neu.charitable;

public class Transaction {

    public Integer amount;
    public String charityId, donorUsername;


    public Transaction(Integer amount, String charityId, String donorUsername){
        this.amount = amount;
        this.charityId = charityId;
        this.donorUsername = donorUsername;
    }

}
