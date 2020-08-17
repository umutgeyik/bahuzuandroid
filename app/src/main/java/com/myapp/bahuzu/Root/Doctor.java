package com.myapp.bahuzu.Root;


public class Doctor implements Comparable<Doctor>{
    public String name;
    public String surname;
    public String uid;
    public String phone;
    public String profession;
    public String description;
    public String experiences;
    public String notificationId;
    public String doctorRestriction;
    public String rating;
    public String price;
    public int priority;

    public Doctor(){
        // Default Constructor
    }

    public Doctor(String name, String surname, String uid){
        this.name = name;
        this.surname = surname;
        this.uid = uid;
        this.doctorRestriction = "false";
        this.profession = "";
        this.description = "";
        this.experiences = "";
        this.notificationId = "";
        this.rating = "5";
        this.price = "";
        this.phone = "";
        this.priority = 1;
    }

    public Doctor(String uid,String name){
        this.name = name;
        this.surname ="";
        this.uid = uid;
        this.doctorRestriction = "false";
        this.profession = "";
        this.description = "";
        this.experiences = "";
        this.notificationId = "";
        this.rating = "5";
        this.price = "";
        this.phone = "";
        this.priority = 1;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDoctorRestriction() {
        return doctorRestriction;
    }

    public void setDoctorRestriction(String doctorRestriction) {
        this.doctorRestriction = doctorRestriction;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExperiences() {
        return experiences;
    }

    public void setExperiences(String experiences) {
        this.experiences = experiences;
    }

    @Override
    public int compareTo(Doctor o) {
        if(this.getPriority() > o.getPriority()){
            return 1;
        } else {
            return 0;
        }
    }
}
