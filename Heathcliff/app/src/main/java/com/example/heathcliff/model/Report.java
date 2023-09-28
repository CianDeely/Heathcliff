package com.example.heathcliff.model;

import java.util.Date;

public class Report {
    private String reporterID;
    private String reportedID;
    private String reason;
    private String reportMessage;
    private Boolean resolved;
    private Date reportDate;

    public Report(String reporterID, String reportedID, String reason, String reportMessage, Boolean resolved, Date reportDate){
        this.reportedID = reportedID;
        this.reporterID = reporterID;
        this.reason = reason;
        this.reportMessage = reportMessage;
        this.resolved = resolved;
        this.reportDate = reportDate;
    }

    public Report(){

    }

    public String getReporterID() {
        return reporterID;
    }

    public void setReporterID(String reporterID) {
        this.reporterID = reporterID;
    }

    public String getReportedID() {
        return reportedID;
    }

    public void setReportedID(String reportedID) {
        this.reportedID = reportedID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reporterID='" + reporterID + '\'' +
                ", reportedID='" + reportedID + '\'' +
                ", reason='" + reason + '\'' +
                ", reportMessage='" + reportMessage + '\'' +
                ", resolved=" + resolved +
                '}';
    }
}
