package ru.startandroid.purchaselist.presenters;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import javax.inject.Inject;

import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.AuthenticationFragmentInterface;
import ru.startandroid.purchaselist.views.MainViewInterface;

/**
 * Created by user on 07/08/2017.
 */

public class AuthenticationPresenterImpl implements AuthPresenter{


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private MainViewInterface mainView;
    private AuthenticationFragmentInterface authView;

    @Inject
    public AuthenticationPresenterImpl(FirebaseAuth fAuth, FirebaseDatabase database,
                                       MainViewInterface mainView, AuthenticationFragmentInterface authView){
        this.firebaseAuth = fAuth;
        this.database = database;
        this.mainView = mainView;
        this.authView = authView;
    }
    @Override
    public void signUp(String email, String password, String username, SharedPreferences preferences) {

        if(!verifyIsEmptyPrivateData(email, password, username)) {
            return;
        }else if(checkIsNameAlreadyPresent(username)){
            mainView.showMessage("Sorry, but this Name already exists");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                saveUser(email, username, preferences);
                mainView.showMessage("You've signed up");
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
        if(checkUser(username, email)){
            mainView.showMessage("Type Your Name, please");
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                saveUser(email, username, preferences);
                mainView.showMessage("You've logged in");
                mainView.openAccountView(true);
            } else {
                mainView.showMessage("Authentication failed");
            }
        });
    }
    private boolean checkIsNameAlreadyPresent(String name){
        List<UserInformation> usersData = authView.getUsersData();
        for (UserInformation it : usersData) {
            if(name.equals(it.getName()))
                return true;
        }
        return false;
    }
    private boolean verifyIsEmptyPrivateData(String email, String password, String userName){
        final String eKey = "email";
        final String pKey = "password";
        final String nKey = "name";
        if(TextUtils.isEmpty(email)){
           authView.showErrorMessage(eKey);
            return false;
        } else if(TextUtils.isEmpty(password)) {
            authView.showErrorMessage(pKey);
            return false;
        }else if (TextUtils.isEmpty(userName)){
            authView.showErrorMessage(nKey);
            return false;
        }
        return true;
    }
    private void saveUser(String email, String username, SharedPreferences preferences) {
        UserInformation userInfo = new UserInformation(email, username, firebaseAuth.getCurrentUser().getUid());
        preferences.edit().putString("current user name", username).commit();
        preferences.edit().putString("current user email", email).commit();
        database.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInfo);
    }
    private boolean checkUser(String userName, String userEmail){
        for (UserInformation it : authView.getUsersData()) {
            if(it.getEmail().equals(userEmail) && !it.getName().equals(userName))
                return true;
        }
        return false;
    }
}
