package ru.startandroid.purchaselist.presenters;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 07/08/2017.
 */

public interface AuthPresenter {

    void signUp(String email, String password, String username, SharedPreferences preferences);
    void logIn(String email, String password, String username, SharedPreferences preferences);
    void fetchAllUsersData(List<UserInformation> usersData);
}
