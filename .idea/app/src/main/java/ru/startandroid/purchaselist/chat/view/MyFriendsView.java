package ru.startandroid.purchaselist.chat.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.startandroid.purchaselist.R;

/**
 * Created by user on 16/03/2018.
 */

public class MyFriendsView extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_friends_view, null);
        return v;
    }
}
