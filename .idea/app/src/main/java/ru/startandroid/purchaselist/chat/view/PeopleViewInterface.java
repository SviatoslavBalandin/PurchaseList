package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 26/03/2018.
 */

public interface PeopleViewInterface {
    void showPeopleList();
    List<UserInformation> getPeopleList();
    List<UserInformation> getInvitedPersonsList();
    void refreshList();
    SharedPreferences getPreferences();
}
