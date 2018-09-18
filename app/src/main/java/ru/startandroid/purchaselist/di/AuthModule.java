package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerFragment;
import ru.startandroid.purchaselist.presenters.AuthPresenter;
import ru.startandroid.purchaselist.presenters.AuthenticationPresenterImpl;
import ru.startandroid.purchaselist.views.AuthenticationFragmentInterface;
import ru.startandroid.purchaselist.views.MainViewInterface;

/**
 * Created by user on 07/08/2017.
 */
@Module
public class AuthModule {
    private final MainViewInterface mainView;
    private final AuthenticationFragmentInterface authenticationFragment;

    public AuthModule(MainViewInterface mainView, AuthenticationFragmentInterface authenticationFragment) {
        this.authenticationFragment = authenticationFragment;
        this.mainView = mainView;
    }
    @PerFragment
    @Provides
    AuthPresenter provideAuthPresenter(FirebaseAuth mAuth, FirebaseDatabase database){
        return new AuthenticationPresenterImpl(mAuth, database, mainView, authenticationFragment);
    }
}
