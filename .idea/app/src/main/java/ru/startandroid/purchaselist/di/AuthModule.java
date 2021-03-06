package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.presenters.AuthenticationPresenterImpl;
import ru.startandroid.purchaselist.views.MainViewInterface;
import ru.startandroid.purchaselist.views.ScreenView;
import ru.startandroid.purchaselist.views.MainActivity;

/**
 * Created by user on 07/08/2017.
 */
@Module
public class AuthModule {
    private final MainViewInterface mainView;
    private final ScreenView screenView;

    public AuthModule(MainViewInterface mainView, ScreenView screenView) {
        this.screenView = screenView;
        this.mainView = mainView;
    }

    @Provides
    AuthenticationPresenterImpl provideAuthPresenter(FirebaseAuth mAuth, FirebaseDatabase database){
        return new AuthenticationPresenterImpl(mAuth, database, mainView, screenView);
    }
}
