package com.example.heathcliff.model;

public class Admin {
    private String userID;

    public Admin(String userID){
        this.userID = userID;
    }

    public Admin(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userID='" + userID + '\'' +
                '}';
    }
}
