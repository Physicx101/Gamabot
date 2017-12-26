package com.example.prabowo.gamabot;

/**
 * Created by Fauziw97 on 11/18/17.
 */

public class UserModel {
    private String name;
    private String email;
    private String uId;
    private String photoUrl;

    public UserModel() {
    }

    public UserModel(String name, String email, String uId, String photoUrl) {
        this.name = name;
        this.email = email;
        this.uId = uId;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

