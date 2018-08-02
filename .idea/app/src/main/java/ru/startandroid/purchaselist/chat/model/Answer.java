package ru.startandroid.purchaselist.chat.model;

/**
 * Created by user on 26/03/2018.
 */

public class Answer {

    private String listId;
    private String userId;
    private boolean content;
    private String answerId;

    public Answer(String answerId, String listId, String userId, boolean content){
        this.listId = listId;
        this.userId = userId;
        this.content = content;
        this.answerId = answerId;
    }

    public void setContent(boolean content) {
        this.content = content;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public boolean isContent() {

        return content;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public void setListId(String listId) {

        this.listId = listId;
    }

    public boolean getContent() {

        return content;
    }

    public String getUserId() {

        return userId;
    }

    public String getListId() {

        return listId;
    }
}
