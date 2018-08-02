package ru.startandroid.purchaselist.views;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.model.Purchase;

/**
 * Created by user on 04/02/2018.
 */

public interface ShoppingListView {
    String getListId();
    List<Purchase> getGoodsList();
    SharedPreferences getMainPreferences();
    SharedPreferences getPrivatePreferences();
    void refreshList();
    void showProducts();
    void verifyCheckedItems(boolean isChecked, String id);
}
