package com.example.heathcliff.model;

import java.sql.Timestamp;

public class Picture {
    private String UserID;
    private String accessToken;
    private String type;
    private int order;
    private boolean profilePicture;
    private String profileID;

    public Picture(String UserID, String accessToken,  String type, int order, boolean profilePicture, String profileID){
        this.UserID = UserID;
        this.accessToken = accessToken;
        this.type = type;
        this.order = order;
        this.profilePicture = profilePicture;
        this.profileID = profileID;

    }

    public Picture(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(boolean profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "UserID='" + UserID + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", type='" + type + '\'' +
                ", order=" + order +
                ", profilePicture=" + profilePicture +
                '}';
    }
}
