package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.views.AuthenticationFragment;

/**
 * Created by user on 25/01/2018.
 */
@Subcomponent(modules = {AuthModule.class})
public interface AuthComponent {

    void inject(AuthenticationFragment fragment);
}
