package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerActivity;
import ru.startandroid.purchaselist.presenters.MainActivityPresenter;
import ru.startandroid.purchaselist.presenters.MainActivityPresenterImpl;

/**
 * Created by user on 25/01/2018.
 */
@Module
public class MainActivityModule {

    @PerActivity
    @Provides
    MainActivityPresenter provideMainActivityPresenter(FirebaseAuth auth){
        return new MainActivityPresenterImpl(auth);
    }

}
