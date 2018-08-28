package ru.startandroid.purchaselist.di.fireBaseModule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerApp;

@Module
public class FirebaseModule {

    @PerApp
    @Provides
    FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @PerApp
    @Provides
    FirebaseDatabase provideFirebaseDatabase(){
        return  FirebaseDatabase.getInstance();
    }

}
