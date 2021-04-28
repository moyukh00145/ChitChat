package com.example.chitchat;

public class user {

    private String name;
    private String email;
    private String uid;
    private String imgurl;
    private String about;
    private String token;

    public user(String name, String email, String uid,String imgurl,String about,String token) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.imgurl=imgurl;
        this.about=about;
        this.token=token;
    }

    public user() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
