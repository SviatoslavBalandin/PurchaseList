package ru.startandroid.purchaselist.chat.model;

/**
 * Created by user on 22/03/2018.
 */

public class Message {

    private String id;
    private String content;
    private String date;
    private String nameOfCreator;

    public Message(){}

    public Message(String id, String content, String date, String nameOfCreator){
        this.id = id;
        this.content = content;
        this.date = date;
        this.nameOfCreator = nameOfCreator;
    }

    public String getDate() {

        return date;
    }

    public String getContent() {

        return content;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }

    public String getId() {

        return id;

    }
}
