package com.example.heathcliff.model;

public class Match {
    private String adopterID;
    private String petID;
    private String userID;
    private String adopterUserID;
    private int response;

    public Match(String adopterID, String petID, String userID, String adopterUserID, int response){
        this.adopterID = adopterID;
        this.petID = petID;
        this.userID = userID;
        this.adopterUserID = adopterUserID;
        this.response = response;
    }
    public Match(){

    }

    public String getAdopterID() {
        return adopterID;
    }

    public void setAdopterID(String adopterID) {
        this.adopterID = adopterID;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdopterUserID() {
        return adopterUserID;
    }

    public void setAdopterUserID(String adopterUserID) {
        this.adopterUserID = adopterUserID;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "Match{" +
                "adopterID='" + adopterID + '\'' +
                ", petID='" + petID + '\'' +
                ", userID='" + userID + '\'' +
                ", response=" + response +
                '}';
    }
}
