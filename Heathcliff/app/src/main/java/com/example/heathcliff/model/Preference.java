package com.example.heathcliff.model;

public class Preference {
    private String userID;
    private String type;
    private String breed;
    private String coat;
    private int maxAge;
    private int maxDistance;

    public Preference (String userID, String type, String breed, String coat, int maxAge, int maxDistance){
        userID = this.userID;
        type = this.type;
        breed = this.breed;
        coat = this.coat;
        maxAge = this.maxAge;
        maxDistance = this.maxDistance;
    }

    public Preference(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getCoat() {
        return coat;
    }

    public void setCoat(String coat) {
        this.coat = coat;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public String toString() {
        return "Preference{" +
                "userID='" + userID + '\'' +
                ", type='" + type + '\'' +
                ", breed='" + breed + '\'' +
                ", coat='" + coat + '\'' +
                ", maxAge=" + maxAge +
                ", maxDistance=" + maxDistance +
                '}';
    }
}
