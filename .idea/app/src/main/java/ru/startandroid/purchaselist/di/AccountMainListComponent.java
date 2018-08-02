package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.views.AccountFragment;

/**
 * Created by user on 07/08/2017.
 */
@Subcomponent(modules = {AccountMainListModule.class})
public interface AccountMainListComponent {
    void inject (AccountFragment accountFragment);
}
