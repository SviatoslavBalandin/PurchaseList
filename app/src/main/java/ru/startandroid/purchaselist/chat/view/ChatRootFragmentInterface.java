package ru.startandroid.purchaselist.chat.view;

import android.app.FragmentManager;
import android.content.SharedPreferences;

import ru.startandroid.purchaselist.model.GoodsList;

/**
 * Created by user on 23/03/2018.
 */

public interface ChatRootFragmentInterface {
    void backToList();
    SharedPreferences getMainPreferences();
    SharedPreferences getPrivatePreferences();
    FragmentManager getMainFragmentManager();
    GoodsList getParentList();
}
