package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.PermissionView;
import ru.startandroid.purchaselist.di.annotations.PerFragment;

/**
 * Created by user on 10/04/2018.
 */
@PerFragment
@Subcomponent(modules = {PermissionsModule.class})
public interface PermissionsComponent {
    void inject(PermissionView view);
}
