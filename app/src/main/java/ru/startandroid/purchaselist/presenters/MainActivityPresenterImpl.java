package ru.startandroid.purchaselist.presenters;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

/**
 * Created by user on 25/01/2018.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter{

    FirebaseAuth firebaseAuth;

    @Inject
    public MainActivityPresenterImpl(FirebaseAuth firebaseAuth){
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public boolean accountGateSwitch() {

        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public void logOut() {
        firebaseAuth.signOut();
    }
}
