package ru.startandroid.purchaselist.chat.presenters;

import java.util.List;

import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 26/03/2018.
 */

public interface PersonListPresenter {
    void fetchPersons(boolean onCreateView);
    int invitePersons(List<UserInformation> invitedPersons);
    List<UserInformation> filter(String query);
    boolean checkPerson(UserInformation person);

}
