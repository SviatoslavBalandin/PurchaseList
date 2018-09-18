package ru.startandroid.purchaselist.chat.view;

import java.util.List;

import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 10/04/2018.
 */

public interface PermissionViewInterface {
    void showDialogGuests();
    void refreshGuestsList();
    List<UserInformation> getDialogGuestsList();
    List<UserInformation> getUselessGuests();
    GoodsList getParentList();
    void activateDeleteButton();

}
