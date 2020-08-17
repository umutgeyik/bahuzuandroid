package com.myapp.bahuzu.Root;


public class DoctorRandevu {
    private String hour;
    private String date;
    private String status;
    private String patientName;
    private String doctorName;
    private boolean isChecked;
    private String doctorUid;

    public DoctorRandevu(){
        // Default Constructor
    }

    public DoctorRandevu(String hour,String status,String patientName,Boolean isChecked){
        this.hour = hour;
        this.status = status;
        this.patientName = patientName;
        this.isChecked = isChecked;
        this.doctorName = "";
        this.doctorUid = "";
        this.date = "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}

