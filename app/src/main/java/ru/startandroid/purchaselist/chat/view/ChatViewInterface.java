package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;

import java.util.List;

import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.model.GoodsList;

/**
 * Created by user on 22/03/2018.
 */

public interface ChatViewInterface {
    List<Message> getMessageList();
    void refreshList();
    SharedPreferences getPreferences();
    SharedPreferences getPrivatePreferences();
    GoodsList getParentList();

}
