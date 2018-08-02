package ru.startandroid.purchaselist.chat.presenters;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 10/04/2018.
 */

public interface PermissionPresenter {
    void fetchDialogGuests();
    void deleteDialogGuests(List<UserInformation> fellows);
    boolean checkPerson(int position);
}
