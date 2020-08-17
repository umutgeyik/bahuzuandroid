package com.myapp.bahuzu.Root;

public class Rating {
    private String doctorUid;
    private String userUid;
    private String rating;

    public Rating() {
        //Default constr.
    }

    public Rating(String doctorUid, String userUid, String rating) {
        this.doctorUid = doctorUid;
        this.userUid = userUid;
        this.rating = rating;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
