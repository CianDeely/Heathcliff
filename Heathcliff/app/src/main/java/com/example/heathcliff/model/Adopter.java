package com.example.heathcliff.model;

import java.sql.Timestamp;
import java.util.Date;

public class Adopter {
    private Date dob;
    private String gender;
    private String aboutMe;
    private String occupation;
    private String locationID;
    private String userID;

    public Adopter(Date dob, String gender, String aboutMe, String occupation, String locationID, String userID){
        this.dob = dob;
        this.gender = gender;
        this.aboutMe = aboutMe;
        this.occupation = occupation;
        this.locationID = locationID;
        this.userID = userID;
    }

    public Adopter(){

    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Adopter{" +
                "dob=" + dob +
                ", gender='" + gender + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", occupation='" + occupation + '\'' +
                ", locationID='" + locationID + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
