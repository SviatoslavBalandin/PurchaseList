package ru.startandroid.purchaselist.model;

/**
 * Created by user on 05/02/2018.
 */

public class GoodsList implements GoodsListInterface {

    private String date;
    private String title;
    private String listId;
    private UserInformation owner;
    private long productsAmount;
    private String connectionId;
    public boolean isOwner;


    public GoodsList(){}

    public GoodsList(String title, String date, String listId, UserInformation owner, boolean isOwner){
        this.title = title;
        this.date = date;
        this.listId = listId;
        this.owner = owner;
        this.isOwner = isOwner;
        productsAmount = 0;
        connectionId = "";
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {

        return title;
    }

    @Override
    public String getDate() {

        return date;
    }

    @Override
    public UserInformation getOwner() {
        return owner;
    }

    @Override
    public long getProductsAmount() {
        return productsAmount;
    }

    @Override
    public void setProductsAmount(long amount) {
        productsAmount = amount;
    }

    @Override
    public String getListId() {
        return listId;
    }

    public void setOwner(UserInformation owner) {
        this.owner = owner;
    }

    public void setListId(String listId) {

        this.listId = listId;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionId() {

        return connectionId;
    }
}
