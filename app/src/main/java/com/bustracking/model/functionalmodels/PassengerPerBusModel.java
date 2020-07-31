package com.bustracking.model.functionalmodels;

public class PassengerPerBusModel
{
    private String userID;
    private int numOfEntering;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getNumOfEntering() {
        return numOfEntering;
    }

    public void setNumOfEntering(int numOfEntering) {
        this.numOfEntering = numOfEntering;
    }

    public PassengerPerBusModel(String userID, int numOfEntering) {
        this.userID = userID;
        this.numOfEntering = numOfEntering;
    }

    public PassengerPerBusModel() {
    }
}
