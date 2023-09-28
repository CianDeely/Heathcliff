package com.example.heathcliff.model;

import java.util.Date;

public class Message {
    private String chatID;
    private String recipientID;
    private String senderID;
    private String message;
    private Date timeSent;

    public Message(String chatID, String recipientID, String senderID, String message, Date timeSent){
        this.chatID = chatID;
        this.recipientID = recipientID;
        this.senderID = senderID;
        this.message = message;
        this.timeSent = timeSent;
    }

    public Message(){

    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    @Override
    public String toString() {
        return "Message{" +
                "chatID='" + chatID + '\'' +
                "recipientID='" + recipientID + '\'' +
                ", senderID='" + senderID + '\'' +
                ", message='" + message + '\'' +
                ", timeSent=" + timeSent +
                '}';
    }


}
