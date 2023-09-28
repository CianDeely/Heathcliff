package com.example.heathcliff.model;

public class Shelter {
    String shelterName;
    String shelterAddress;
    String userID;

    public Shelter(String shelterName, String shelterAddress, String userID){
        this.shelterName = shelterName;
        this.shelterAddress = shelterAddress;
        this.userID = userID;
    }
    public Shelter(){

    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getShelterAddress() {
        return shelterAddress;
    }

    public void setShelterAddress(String shelterAddress) {
        this.shelterAddress = shelterAddress;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "shelterName='" + shelterName + '\'' +
                ", shelterAddress='" + shelterAddress + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
