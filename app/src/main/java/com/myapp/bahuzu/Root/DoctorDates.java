package com.myapp.bahuzu.Root;

public class DoctorDates {

    private String date;
    private int okStatus;

    public DoctorDates(){
        // Default Constructor
    }

    public DoctorDates(String date,int okStatus ){
        this.date = date;
        this.okStatus = okStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOkStatus() {
        return okStatus;
    }

    public void setOkStatus(int okStatus) {
        this.okStatus = okStatus;
    }
}
