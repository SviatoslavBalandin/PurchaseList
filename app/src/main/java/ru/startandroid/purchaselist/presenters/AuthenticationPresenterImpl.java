package ru.startandroid.purchaselist.presenters;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.MainViewInterface;
import ru.startandroid.purchaselist.views.ScreenView;

/**
 * Created by user on 07/08/2017.
 */

public class AuthenticationPresenterImpl implements AuthPresenter{


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private MainViewInterface mainView;
    private ScreenView screenView;

    @Inject
    public AuthenticationPresenterImpl(FirebaseAuth fAuth, FirebaseDatabase database,
                                       MainViewInterface mainView, ScreenView authView){
        this.firebaseAuth = fAuth;
        this.database = database;
        this.mainView = mainView;
        this.screenView = authView;

    }
    @Override
    public void signUp(String email, String password, String username, SharedPreferences preferences) {

        if(!verifyIsEmptyPrivateData(email, password, username)) {
            mainView.showMessage("Failed verification");
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                savePrivateUserInformation(email, username, preferences);
                mainView.showMessage("You have logged up");
                mainView.openAccountView(true);
            }else{
                mainView.showMessage("Authentication failed");
            }
        });
    }
    @Override
    public void logIn(String email, String password, String username, SharedPreferences preferences) {

        if(!verifyIsEmptyPrivateData(email, password, username)) {
            mainView.showMessage("Failed verification");
            return ;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                savePrivateUserInformation(email, username, preferences);
                mainView.showMessage("You have logged in");
                mainView.openAccountView(true);
            } else {
                mainView.showMessage("Authentication failed");
            }
        });
    }
    private boolean verifyIsEmptyPrivateData(String email, String password, String userName){
        final String eKey = "email";
        final String pKey = "password";
        final String nKey = "name";
        if(TextUtils.isEmpty(email)){
           screenView.showErrorMessage(eKey);
            return false;
        } else if(TextUtils.isEmpty(password)) {
            screenView.showErrorMessage(pKey);
            return false;
        }else if (TextUtils.isEmpty(userName)){
            screenView.showErrorMessage(nKey);
            return false;
        }
        return true;
    }

    private void savePrivateUserInformation(String email, String username, SharedPreferences preferences) {
        UserInformation userInfo = new UserInformation(email, username, firebaseAuth.getCurrentUser().getUid());
        preferences.edit().putString("current user name", username).commit();
        preferences.edit().putString("current user email", email).commit();
        database.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInfo);
    }
}
