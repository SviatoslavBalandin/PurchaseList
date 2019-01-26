package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerFragment;
import ru.startandroid.purchaselist.presenters.AccountPresenter;
import ru.startandroid.purchaselist.presenters.AccountPresenterImpl;
import ru.startandroid.purchaselist.views.AccountScreenView;
import ru.startandroid.purchaselist.views.helpers.MainListAdapter;

@Module
public class AccountMainListModule {

    private final AccountScreenView accountScreenView;

    public AccountMainListModule(AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
    }

    @PerFragment
    @Provides
    AccountPresenter provideAccountPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new AccountPresenterImpl(database, auth, accountScreenView);
    }
}
