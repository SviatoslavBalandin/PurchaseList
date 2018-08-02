package ru.startandroid.purchaselist.di.fireBaseModule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {
    @Provides
    FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    FirebaseDatabase provideFirebaseDatabase(){
        return  FirebaseDatabase.getInstance();
    }

}
