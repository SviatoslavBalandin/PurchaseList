package ru.startandroid.purchaselist.chat.model;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 22/03/2018.
 */

public class Message {

    private String id;
    private String content;
    private String data;
    private String nameOfCreator;

    public Message(){}

    public Message(String id, String content, String data, String nameOfCreator){
        this.id = id;
        this.content = content;
        this.data = data;
        this.nameOfCreator = nameOfCreator;
    }

    public String getData() {

        return data;
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
