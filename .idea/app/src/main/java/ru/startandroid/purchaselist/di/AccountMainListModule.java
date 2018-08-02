package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.presenters.AccountPresenterImpl;
import ru.startandroid.purchaselist.views.AccountScreenView;

/**
 * Created by user on 19/11/2017.
 */
@Module
public class AccountMainListModule {

    private final AccountScreenView accountScreenView;

    public AccountMainListModule(AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
    }

    @Provides
    public AccountPresenterImpl provideAccountPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new AccountPresenterImpl(database, auth, accountScreenView);
    }
}
