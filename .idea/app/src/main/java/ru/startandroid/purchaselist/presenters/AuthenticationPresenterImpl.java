package ru.startandroid.purchaselist.presenters;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private UserInformation userInfo;
    private final String SHOPPING_USERS_KEY = "Users";
    private final String CURRENT_USER_NAME = "current user name";
    private final String CURRENT_USER_EMAIL = "current user email";

    @Inject
    public AuthenticationPresenterImpl(FirebaseAuth fAuth, FirebaseDatabase database,
                                       MainViewInterface mainView, ScreenView authView){
        this.firebaseAuth = fAuth;
        this.database = database;
        this.mainView = mainView;
        this.screenView = authView;

    }
    @Override
    public void logUp(String email, String password, String username, SharedPreferences preferences) {

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
        boolean valid = true;
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
        return valid;
    }
    private FirebaseAuth.AuthStateListener getAuthStateListener(){
        return (firebaseAuth) -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d("myLog", "User signed_in");
            } else {
                Log.d("myLog", "User signed_out");
            }
        };
    }
    private void savePrivateUserInformation(String email, String username, SharedPreferences preferences) {
        userInfo = new UserInformation(email, username, firebaseAuth.getCurrentUser().getUid());
        preferences.edit().putString(CURRENT_USER_NAME, username).commit();
        preferences.edit().putString(CURRENT_USER_EMAIL, email).commit();
        database.getReference().child(SHOPPING_USERS_KEY).child(firebaseAuth.getCurrentUser().getUid()).setValue(userInfo);
    }
    public void connectDBtoStateListener() {
        firebaseAuth.addAuthStateListener(getAuthStateListener());
    }
    public void disconnectDBtoStateListener(){
        firebaseAuth.removeAuthStateListener(getAuthStateListener());
    }
}
