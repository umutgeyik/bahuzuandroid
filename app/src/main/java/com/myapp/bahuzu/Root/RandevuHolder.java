package com.myapp.bahuzu.Root;

public class RandevuHolder {
    private String date;
    private int hourPosition;
    private String doctorUid;
    private String hour;
    private String doctorFullName;

    public RandevuHolder(String date, int hourPosition, String doctorUid,String hour,String doctorFullName) {
        this.date = date;
        this.hourPosition = hourPosition;
        this.doctorUid = doctorUid;
        this.hour = hour;
        this.doctorFullName = doctorFullName;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
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

    public int getHourPosition() {
        return hourPosition;
    }

    public void setHourPosition(int hourPosition) {
        this.hourPosition = hourPosition;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }
}
