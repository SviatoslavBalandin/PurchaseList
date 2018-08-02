package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.views.MainActivity;

/**
 * Created by user on 25/01/2018.
 */
@Subcomponent(modules = {MainActivityModule.class})
public interface MainComponent{

    void inject(MainActivity activity);
}
