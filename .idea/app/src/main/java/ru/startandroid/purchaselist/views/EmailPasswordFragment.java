package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.di.AuthModule;
import ru.startandroid.purchaselist.presenters.AuthenticationPresenterImpl;

/**
 * Created by user on 12/10/2017.
 */

public class EmailPasswordFragment extends Fragment implements ScreenView {

    @Inject
    AuthenticationPresenterImpl presenter;

    @BindView(R.id.btnLogUp) Button btnLogUp;
    @BindView(R.id.btnLogIn) Button btnLogIn;
    @BindView(R.id.emailAddress) EditText etEmailAddress;
    @BindView(R.id.password) EditText etPassword;
    @BindView(R.id.etUserName) EditText etUserName;

    private MainViewInterface mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View v  = inflater.inflate(R.layout.email_password, null);
        ButterKnife.bind(this, v);
        resolveDependencies();
        mainView = (MainViewInterface) getActivity();
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createAuthComponent(new AuthModule((MainViewInterface) getActivity(),this)).inject(this);
    }
    @Override
    public void onStart(){
        super.onStart();
        if(presenter == null) {
            return;
        }
        presenter.connectDBtoStateListener();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(presenter == null) {
            return;
        }
        presenter.disconnectDBtoStateListener();
    }
    @OnClick(R.id.btnLogUp)
    public void logUp() {
        presenter.logUp(etEmailAddress.getText().toString(), etPassword.getText().toString(),
                etUserName.getText().toString(), getPreferences());
    }
    @OnClick(R.id.btnLogIn)
    public void logIn(){
       presenter.logIn(etEmailAddress.getText().toString(), etPassword.getText().toString(),
                etUserName.getText().toString(), getPreferences());
    }
    @Override
    public void showReport(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showErrorMessage(String key) {
        final String message = "Required";

        if(key.equals("email")) {
             etEmailAddress.setError(message);
        }else if(key.equals("password")){
            etPassword.setError(message);
        }else if(key.equals("name")){
            etUserName.setError(message);
        }
    }
    public SharedPreferences getPreferences(){
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }
}

