package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.chat.model.Message;

/**
 * Created by user on 22/03/2018.
 */

public interface ChatViewInterface {
    List<Message> getMessageList();
    void showMessages();
    void refreshList();
    SharedPreferences getPreferences();

}
