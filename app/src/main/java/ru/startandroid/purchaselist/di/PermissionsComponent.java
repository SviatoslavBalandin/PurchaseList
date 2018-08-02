package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.PermissionView;

/**
 * Created by user on 10/04/2018.
 */
@Subcomponent(modules = {PermissionsModule.class})
public interface PermissionsComponent {
    void inject(PermissionView view);
}
