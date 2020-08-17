package com.myapp.bahuzu.Root;

public class User {

    public String name;
    public String surname;
    public String phone;
    public String uid;
    public String notificationId;

    public User(){
        // Default Constructor
    }

    public User(String name, String surname,String uid) {
        this.name = name;
        this.surname = surname;
        this.uid = uid;
        this.phone = "";
        this.notificationId = "";
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
