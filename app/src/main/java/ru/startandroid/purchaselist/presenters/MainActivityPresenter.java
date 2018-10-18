package ru.startandroid.purchaselist.presenters;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 25/01/2018.
 */

public interface MainActivityPresenter {
    boolean accountGateSwitch();
    void signOut();
    void fetchAllUsersData(List<UserInformation> usersData);
}
