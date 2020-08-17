package com.myapp.bahuzu.Root;

public class ErtelemeTalebi {

    private String doctorUid;
    private String userUid;
    private String hour;
    private String date;
    private String senderIdentity;

    public ErtelemeTalebi(){
        //Default Constructor
    }

    public ErtelemeTalebi(String doctorUid,String userUid, String hour, String date, String senderIdentity){
        this.doctorUid = doctorUid;
        this.userUid = userUid;
        this.hour = hour;
        this.date = date;
        this.senderIdentity = senderIdentity;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderIdentity() {
        return senderIdentity;
    }

    public void setSenderIdentity(String senderIdentity) {
        this.senderIdentity = senderIdentity;
    }
}
