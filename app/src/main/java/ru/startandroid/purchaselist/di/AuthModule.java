package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerFragment;
import ru.startandroid.purchaselist.presenters.AuthPresenter;
import ru.startandroid.purchaselist.presenters.AuthenticationPresenterImpl;
import ru.startandroid.purchaselist.views.MainViewInterface;
import ru.startandroid.purchaselist.views.ScreenView;

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
    @PerFragment
    @Provides
    AuthPresenter provideAuthPresenter(FirebaseAuth mAuth, FirebaseDatabase database){
        return new AuthenticationPresenterImpl(mAuth, database, mainView, screenView);
    }
}
