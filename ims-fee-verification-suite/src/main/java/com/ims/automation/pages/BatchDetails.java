package com.ims.automation.pages;

public class BatchDetails {

    private String batchName;
    private String fee;
    private String gst;
    private String startDate;
    private String batchType;

    public BatchDetails() {

    }

    public BatchDetails(String batchName,
                        String fee,
                        String gst,
                        String startDate,
                        String batchType) {

        this.batchName = batchName;
        this.fee = fee;
        this.gst = gst;
        this.startDate = startDate;
        this.batchType = batchType;

    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    @Override
    public String toString() {

        return "BatchDetails {" +
                "batchName='" + batchName + '\'' +
                ", fee='" + fee + '\'' +
                ", gst='" + gst + '\'' +
                ", startDate='" + startDate + '\'' +
                ", batchType='" + batchType + '\'' +
                '}';

    }

}