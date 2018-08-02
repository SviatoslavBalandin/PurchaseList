package ru.startandroid.purchaselist.di;

import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.chat.presenters.PermissionPresenter;
import ru.startandroid.purchaselist.chat.presenters.PermissionPresenterImpl;
import ru.startandroid.purchaselist.chat.view.PermissionViewInterface;

/**
 * Created by user on 10/04/2018.
 */
@Module
public class PermissionsModule {

    private final PermissionViewInterface permissionView;

    public PermissionsModule(PermissionViewInterface permissionView){
        this.permissionView = permissionView;
    }
    @Provides
    public PermissionPresenter providePermissionPresenter(FirebaseDatabase database){
        return new PermissionPresenterImpl(permissionView, database);
    }

}
