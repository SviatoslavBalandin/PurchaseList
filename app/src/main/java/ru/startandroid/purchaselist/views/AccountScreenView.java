package ru.startandroid.purchaselist.views;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.model.GoodsList;

/**
 * Created by user on 01/12/2017.
 */

public interface AccountScreenView {
    SharedPreferences getMainPreferences();
    SharedPreferences getPrivatePreferences();
    List<GoodsList> getMainList();
    void showProducts();
    void refreshList();
}
