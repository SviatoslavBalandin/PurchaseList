package ru.startandroid.purchaselist.views;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import ru.startandroid.purchaselist.R;

/**
 * Created by user on 06/03/2018.
 */

public class PreferenceView  extends PreferenceFragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }


}
