package ru.startandroid.purchaselist.chat.presenters;

/**
 * Created by user on 22/03/2018.
 */

public interface ChatPresenter {

    void deleteMessage(String messageId);
    void addMessage(String content);
    void fetchAllMessages();
}
