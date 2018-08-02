package ru.startandroid.purchaselist.chat.model;

/**
 * Created by user on 26/03/2018.
 */

public class Invitation {

    private String id;
    private String senderId;
    private String listId;
    private String content;
    private String senderName;
    private String date;

    public Invitation(String id, String senderId, String senderName, String listId, String content, String date){
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.date = date;
        this.listId = listId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setListId(String listId) {

        this.listId = listId;
    }

    public void setSenderId(String senderId) {

        this.senderId = senderId;
    }
    public void setContent(String content) {

        this.content = content;
    }
    public String getContent() {

        return content;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {

        return senderName;
    }

    public String getListId() {

        return listId;
    }

    public String getSenderId() {

        return senderId;
    }
}
