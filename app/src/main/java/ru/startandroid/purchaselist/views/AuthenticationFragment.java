package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.startandroid.purchaselist.presenters.AuthPresenter;

/**
 * Created by user on 12/10/2017.
 */

public class AuthenticationFragment extends Fragment implements ScreenView {

    @Inject
    AuthPresenter presenter;

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
        mainView = (MainViewInterface) getActivity();
        resolveDependencies();
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createAuthComponent(new AuthModule((MainViewInterface) getActivity(),this)).inject(this);
    }

    @OnClick(R.id.btnLogUp)
    public void logUp() {
        presenter.signUp(etEmailAddress.getText().toString(), etPassword.getText().toString(),
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

        switch (key){
            case "email":
                etEmailAddress.setError(message);
                break;
            case "password":
                etPassword.setError(message);
                break;
            case "name":
                etUserName.setError(message);
                break;
            default:
                break;
        }
    }
    public SharedPreferences getPreferences(){
        return mainView.getPrivatePreferences();
    }
}

