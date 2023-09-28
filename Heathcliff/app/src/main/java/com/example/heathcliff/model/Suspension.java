package com.example.heathcliff.model;

import java.util.Date;

public class Suspension {
    private String userID;
    private String adminID;
    private String suspensionReason;
    private String suspensionMessage;
    private String suspensionLength;
    private Date endDate;

    public Suspension(String userID, String adminID, String suspensionReason, String suspensionMessage, String suspensionLength, Date endDate){
        this.userID = userID;
        this.adminID = adminID;
        this.suspensionReason = suspensionReason;
        this.suspensionMessage = suspensionMessage;
        this.suspensionLength = suspensionLength;
        this.endDate = endDate;
    }

    public Suspension(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getSuspensionReason() {
        return suspensionReason;
    }

    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    public String getSuspensionMessage() {
        return suspensionMessage;
    }

    public void setSuspensionMessage(String suspensionMessage) {
        this.suspensionMessage = suspensionMessage;
    }

    public String getSuspensionLength() {
        return suspensionLength;
    }

    public void setSuspensionLength(String suspensionLength) {
        this.suspensionLength = suspensionLength;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Suspension{" +
                "userID='" + userID + '\'' +
                ", adminID='" + adminID + '\'' +
                ", suspensionReason='" + suspensionReason + '\'' +
                ", suspensionMessage='" + suspensionMessage + '\'' +
                ", suspensionLength='" + suspensionLength + '\'' +
                ", endDate=" + endDate +
                '}';
    }
}
