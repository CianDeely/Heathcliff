package com.example.heathcliff.model;

public class Swipe {
    private String adopterID;
    private String petID;
    private int like;
    private String userID;
    private String adopterUserID;

    public Swipe(String adopterID, String petID, int like, String userID, String adopterUserID){
        this.adopterID = adopterID;
        this.petID = petID;
        this.like = like;
        this.userID = userID;
        this.adopterUserID = adopterUserID;
    }

    public Swipe(){

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

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
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

    @Override
    public String toString() {
        return "Swipe{" +
                "adopterID='" + adopterID + '\'' +
                ", petID='" + petID + '\'' +
                ", like=" + like +
                '}';
    }
}
