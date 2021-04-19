package com.example.chitchat;

public class lastmessage {
    private String chatroomid;
    private String lastmsg;

    public lastmessage(String chatroomid, String lastmsg) {
        this.chatroomid = chatroomid;
        this.lastmsg = lastmsg;
    }

    public lastmessage() {

    }

    public String getChatroomid() {
        return chatroomid;
    }


    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }
}
