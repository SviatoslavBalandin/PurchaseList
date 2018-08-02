package ru.startandroid.purchaselist.views.helpers;

import android.view.View;

/**
 * Created by user on 11/04/2018.
 */

public interface ClickListener{
    void onClick(View view, int position);
    void onLongClick(View view,int position);
}