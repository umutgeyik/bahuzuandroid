package com.myapp.bahuzu.Root;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Randevu implements Comparable{

    private String doctorUid;
    private String userUid;
    private String date;
    private String hour;
    private String doctorFullName;
    private String userFullName;
    private String randevuId;

    public Randevu(){
        //Default Constructor
    }

    public Randevu(String doctorUid,String userUid, String date, String hour, String doctorFullName, String userFullName){
        this.doctorUid = doctorUid;
        this.userUid = userUid;
        this.date = date;
        this.hour = hour;
        this.doctorFullName = doctorFullName;
        this.userFullName = userFullName;
        this.randevuId = "";
    }

    public String getRandevuId() {
        return randevuId;
    }

    public void setRandevuId(String randevuId) {
        this.randevuId = randevuId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    @Override
    public int compareTo(Object o) {
        String date = this.getDate();
        String hour = this.getHour();

        Randevu rand = (Randevu) o;

        String oDate = ((Randevu) o).getDate();
        String oHour = ((Randevu) o).getHour();
        String oFullDate = oDate + " " + oHour;

        String fullDate = date + " " + hour;
        DateFormat f = new SimpleDateFormat("MM|dd|yyyy hh:mm");
        try {
            return f.parse(fullDate).compareTo(f.parse(oFullDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this.getDate().compareTo(((Randevu) o).getDate());
    }
}
