package ru.startandroid.purchaselist.chat.presenters;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 26/03/2018.
 */

public interface PersonListPresenter {
    void fetchPersons();
    void invitePersons(List<UserInformation> invitedPersons);
    List<UserInformation> filter(String query);
    boolean checkPerson(int position);
    boolean checkInvitedPerson(int position);
}
