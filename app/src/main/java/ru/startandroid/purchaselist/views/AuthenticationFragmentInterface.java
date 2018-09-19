package ru.startandroid.purchaselist.views;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

public interface AuthenticationFragmentInterface {

    void showErrorMessage(String key);
    List<UserInformation> getUsersData();

}
