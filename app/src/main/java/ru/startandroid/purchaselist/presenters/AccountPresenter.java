package ru.startandroid.purchaselist.presenters;

/**
 * Created by user on 19/11/2017.
 */

public interface AccountPresenter {
    void addList();
    void deleteList();
    void deleteList(int position);
    void renameList(String newName, String listId);
    void fetchLists();
    void reactOnAnswer();
    void removeUnusedAnswers();
    void stopListen();

}
