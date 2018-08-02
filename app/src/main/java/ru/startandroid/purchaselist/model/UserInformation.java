package ru.startandroid.purchaselist.model;

/**
 * Created by user on 19/11/2017.
 */

public class UserInformation {
    private String email;
    private String name;
    private String id;

    public UserInformation(){}

    public UserInformation(String email, String name, String id){
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public String getId() { return id; }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
