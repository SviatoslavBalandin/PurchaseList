package ru.startandroid.purchaselist.views;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.di.MainActivityModule;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.presenters.MainActivityPresenterImpl;

/**
 * Created by user on 15/10/2017.
 */

public class MainActivity extends AppCompatActivity implements MainViewInterface {

    @Inject
    MainActivityPresenterImpl presenter;

    @BindView(R.id.main_Drawer_Layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.mainNavigationView)
    NavigationView mainNavigationDrawerView;
    @BindView(R.id.mainActivityToolBar)
    Toolbar toolbar;

    private FragmentManager fragManager;
    private boolean isItLastBorder = false;
    private List<UserInformation> usersData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        resolveDependencies();
        fragManager = getFragmentManager();
        usersData = new ArrayList<>();
        presenter.fetchAllUsersData(usersData);
        toolbar.setNavigationIcon(R.drawable.ic_acc_nav3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> showDrawer());
        mainNavigationDrawerView.setNavigationItemSelectedListener(getNavigationItemSelectedListener());

        if(presenter.accountGateSwitch()){
            openAccountView(true);
        }else
            openEmailPasswordView();
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createMainComponent(new MainActivityModule()).inject(this);
    }

    @Override
    public void openEmailPasswordView() {
        isItLastBorder = true;
        setToolBarVisibility(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        FragmentTransaction transaction = fragManager.beginTransaction();
        if(fragManager.findFragmentById(R.id.frag_container) == null){
            transaction.add(R.id.frag_container, new AuthenticationFragment());
        }else {
            transaction.replace(R.id.frag_container, new AuthenticationFragment());
        }
        transaction.commit();
    }

    @Override
    public void openAccountView(boolean switchStatus) {
        isItLastBorder = switchStatus;
        if(isItLastBorder) {
            attachUserInfoToDrawer();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            setToolBarVisibility(false);
            FragmentTransaction transaction = fragManager.beginTransaction();
            if (fragManager.findFragmentById(R.id.frag_container) == null) {
                transaction.add(R.id.frag_container, new AccountFragment());
            } else {
                transaction.replace(R.id.frag_container, new AccountFragment());
            }
            transaction.commit();
        }
    }

    private void setToolBarVisibility(boolean visible){
        if(!visible)
            toolbar.setVisibility(View.GONE);
        else
            toolbar.setVisibility(View.VISIBLE);
    }


    @Override
    public void showDrawer() {
     drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onSwitch() {
        isItLastBorder = false;
    }

    @Override
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return getPreferences(MODE_PRIVATE);
    }

    @Override
    public boolean getDrawerStatus() {
        return drawerLayout.isDrawerOpen(mainNavigationDrawerView);
    }

    @Override
    public List<UserInformation> getUsersData() {
        return usersData;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(mainNavigationDrawerView)) {
            drawerLayout.closeDrawer(mainNavigationDrawerView);
        }else if(!isItLastBorder){
            openAccountView(true);
        }else
            super.onBackPressed();
    }

    @SuppressLint("WrongConstant")
    private NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener(){
        return item -> {
            switch (item.getItemId()){

                case R.id.drawer_Lists_Item_menu:
                    drawerLayout.closeDrawer(Gravity.START);
                    openAccountView(true);
                    item.setChecked(true);
                    break;

                case R.id.drawer_Calendar_Item_menu:
                    onSwitch();
                    toolbar.setTitle(R.string.calendar);
                    setToolBarVisibility(true);
                    drawerLayout.closeDrawer(Gravity.START);
                    FragmentTransaction transaction = fragManager.beginTransaction();
                    transaction.replace(R.id.frag_container, new CalendarFragment());
                    transaction.commit();
                    item.setChecked(true);
                    break;

                case R.id.drawer_Invitations_Item_menu:
                    onSwitch();
                    setToolBarVisibility(false);
                    drawerLayout.closeDrawer(Gravity.START);
                    transaction = fragManager.beginTransaction();
                    transaction.replace(R.id.frag_container, new InvitationListView());
                    transaction.commit();
                    item.setChecked(true);
                    break;

                case R.id.drawer_Settings_Item_menu:
                    onSwitch();
                    toolbar.setTitle(R.string.settings);
                    setToolBarVisibility(true);
                    drawerLayout.closeDrawer(Gravity.START);
                    transaction = fragManager.beginTransaction();
                    transaction.replace(R.id.frag_container, new PreferenceView());
                    transaction.commit();
                    item.setChecked(true);
                    break;

                case R.id.drawer_LogOut_Item_menu:
                    drawerLayout.closeDrawer(Gravity.START);
                    presenter.signOut();
                    openEmailPasswordView();
                    showMessage("You have Logged Out :(");
                    item.setChecked(true);
                    break;

                default:
                    return false;
            }
            return true;
        };
    }

    private void attachUserInfoToDrawer(){

        ((TextView)mainNavigationDrawerView.getHeaderView(0).findViewById(R.id.userNameHeaderTV))
                .setText(getPrivatePreferences().getString("current user name", ""));

        ((TextView)mainNavigationDrawerView.getHeaderView(0).findViewById(R.id.userEmailHeaderTV))
                .setText(getPrivatePreferences().getString("current user email", ""));
    }
}
