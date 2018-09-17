package ru.startandroid.purchaselist.chat.model;

import java.util.ArrayList;
import java.util.List;

import ru.startandroid.purchaselist.model.GoodsList;

/**
 * Created by user on 31/03/2018.
 */

public class Connection {

    private String id;
    private String ownerId;
    private String listId;
    private List<String> guestsList;

    public Connection(){
        guestsList = new ArrayList<>();
    }

    public Connection(String id, String ownerId, String listId, String firstGuestId){
        this.id = id;
        this.ownerId = ownerId;
        this.listId = listId;
        guestsList = new ArrayList<>();
        guestsList.add(firstGuestId);
    }

    public List<String> getGuestsList() {
        return guestsList;
    }

    public String getListId() {

        return listId;
    }

    public String getOwnerId() {

        return ownerId;
    }

    public String getId() {

        return id;
    }

    public void setList(String listId) {
        this.listId = listId;
    }

    public void setId(String id) {

        this.id = id;
    }
}
