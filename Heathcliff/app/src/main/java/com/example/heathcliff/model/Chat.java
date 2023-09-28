package com.example.heathcliff.model;

public class Chat {
    private String userID;
    private String otherUserID;

    public Chat(String userID, String otherUserID){
        this.userID = userID;
        this.otherUserID = otherUserID;
    }

    public Chat(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }

    @Override
    public String toString() {
        return "Chat{" +
                ", userID='" + userID + '\'' +
                ", otherUserID='" + otherUserID + '\'' +
                '}';
    }
}
