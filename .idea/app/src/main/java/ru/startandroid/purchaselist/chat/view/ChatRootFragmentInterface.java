package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;

/**
 * Created by user on 23/03/2018.
 */

public interface ChatRootFragmentInterface {
    void backToList();
    SharedPreferences getMainPreferences();
    SharedPreferences getPrivatePreferences();
}
