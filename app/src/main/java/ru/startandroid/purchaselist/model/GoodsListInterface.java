package ru.startandroid.purchaselist.model;

import java.util.List;

/**
 * Created by user on 12/02/2018.
 */

public interface GoodsListInterface {

    String getTitle();
    void setTitle(String newTitle);
    String getListId();
    String getDate();
    UserInformation getOwner();
    long getProductsAmount();
    void setProductsAmount(long amount);
}
