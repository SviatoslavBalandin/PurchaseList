package ru.startandroid.purchaselist.presenters;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.presenters.technical_staff.FireFlowableFactory;

/**
 * Created by user on 25/01/2018.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter{

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    @Inject
    public MainActivityPresenterImpl(FirebaseAuth firebaseAuth, FirebaseDatabase database){
        this.firebaseAuth = firebaseAuth;
        this.database = database;
    }

    @Override
    public boolean accountGateSwitch() {

        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchAllUsersData(List<UserInformation> usersData){
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                        .map(child -> child.getValue(UserInformation.class))
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dataList -> usersData.addAll(dataList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
