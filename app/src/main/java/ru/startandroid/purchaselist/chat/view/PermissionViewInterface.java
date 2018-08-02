package ru.startandroid.purchaselist.chat.view;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 10/04/2018.
 */

public interface PermissionViewInterface {
    void showDialogGuests();
    void refreshGuestsList();
    List<UserInformation> getDialogGuestsList();
    List<UserInformation> getUselessGuests();
    String getConnectionId();
    void activateDeleteButton();

}
