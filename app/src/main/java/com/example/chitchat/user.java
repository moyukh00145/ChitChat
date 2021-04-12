package com.example.chitchat;

public class user {

    private String name;
    private String email;
    private String lastmsg;
    private String uid;
    private String imgurl;
    private String about;

    public user(String name, String email, String uid,String lastmsg,String imgurl,String about) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.lastmsg=lastmsg;
        this.imgurl=imgurl;
        this.about=about;
    }

    public user() {
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

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
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
