package ru.startandroid.purchaselist.presenters;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.presenters.technical_staff.FireFlowableFactory;
import ru.startandroid.purchaselist.views.CalendarInterface;

public class CalendarPresenter implements CalendarPresenterInterface {

    private CalendarInterface calendarFragment;
    private String currentUserId;
    private DatabaseReference listsReference;

    @Inject
    public CalendarPresenter(FirebaseDatabase database, FirebaseAuth auth, CalendarInterface calendarFragment) {
        this.calendarFragment = calendarFragment;
        currentUserId = auth.getCurrentUser().getUid();
        listsReference = database.getReference().child("Shopping Lists");
    }
    @SuppressLint("CheckResult")
    @Override
    public void fetchListsNames(String date) {
        listsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                            .map(snap -> snap.getValue(GoodsList.class))
                            .filter(goodsList -> goodsList.getOwner().getId().equals(currentUserId))
                            .filter(goodsList -> goodsList.getDate().equals(date))
                            .map(list -> list.getTitle())
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mainList -> {
                                calendarFragment.getList().clear();
                                calendarFragment.getList().addAll(mainList);
                                calendarFragment.showNames();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
    }
}
