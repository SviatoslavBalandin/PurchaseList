package ru.startandroid.purchaselist.views;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 16/10/2017.
 */

public interface MainViewInterface {
    void openEmailPasswordView();
    void openAccountView(boolean switchStatus);
    void showMessage(String message);
    void showDrawer();
    void onSwitch();
    SharedPreferences getPreferences();
    SharedPreferences getPrivatePreferences();
    boolean getDrawerStatus();
    List<UserInformation> getUsersData();
}
