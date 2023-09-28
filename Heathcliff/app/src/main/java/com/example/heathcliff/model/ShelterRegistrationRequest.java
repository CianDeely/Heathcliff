package com.example.heathcliff.model;

public class ShelterRegistrationRequest {
    private String userID;
    private String shelterName;
    private String shelterAddress;
    private String documentAccessToken;
    private boolean approved;

    public ShelterRegistrationRequest(String userID, String shelterName, String shelterAddress, String documentAccessToken, boolean approved){
        this.userID = userID;
        this.shelterName = shelterName;
        this.shelterAddress = shelterAddress;
        this.documentAccessToken = documentAccessToken;
        this.approved = approved;
    }
    public ShelterRegistrationRequest(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getDocumentAccessToken() {
        return documentAccessToken;
    }

    public void setDocumentAccessToken(String documentAccessToken) {
        this.documentAccessToken = documentAccessToken;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "ShelterRegistrationRequest{" +
                "userID='" + userID + '\'' +
                ", shelterName='" + shelterName + '\'' +
                ", shelterAddress='" + shelterAddress + '\'' +
                ", documentAccessToken='" + documentAccessToken + '\'' +
                ", approved=" + approved +
                '}';
    }
}
