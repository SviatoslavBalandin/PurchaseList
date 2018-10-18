package ru.startandroid.purchaselist;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import ru.startandroid.purchaselist.di.AppComponent;
import ru.startandroid.purchaselist.di.DaggerAppComponent;
import ru.startandroid.purchaselist.di.fireBaseModule.FirebaseModule;

public class MyApp extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseApp.initializeApp(this);
        appComponent = buildAppComponent();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    public static AppComponent getAppComponent(){
        return appComponent;
    }

    private AppComponent buildAppComponent(){
        return DaggerAppComponent.builder()
                .firebaseModule(new FirebaseModule())
                .build();
    }
}
