package ru.startandroid.purchaselist.views;

import java.util.List;

import ru.startandroid.purchaselist.chat.model.Invitation;

/**
 * Created by user on 26/03/2018.
 */

public interface InvitationListViewInterface {
    void showInvitation();
    void refreshInvitationList();
    List<Invitation> getInvitationList();
}
