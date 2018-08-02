package ru.startandroid.purchaselist.presenters;

import ru.startandroid.purchaselist.chat.model.Invitation;

/**
 * Created by user on 26/03/2018.
 */

public interface InvitationListPresenter {
    void fetchInvitation();
    void sendAnswer(Invitation invitation, boolean answer);
    void deleteInvitation(Invitation invitation);
    void deleteAllInvitations();
}
