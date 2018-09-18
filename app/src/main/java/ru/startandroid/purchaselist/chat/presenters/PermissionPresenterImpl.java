package ru.startandroid.purchaselist.chat.presenters;

import android.annotation.SuppressLint;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.view.PermissionViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.presenters.technical_staff.FireFlowableFactory;

/**
 * Created by user on 10/04/2018.
 */

public class PermissionPresenterImpl implements PermissionPresenter{

    private PermissionViewInterface permissionView;
    private DatabaseReference guestListReference;
    private DatabaseReference usersReference;
    private DatabaseReference concreteConnection;

    public PermissionPresenterImpl(PermissionViewInterface permissionView, FirebaseDatabase database){
        this.permissionView = permissionView;

        if(permissionView.getParentList().getListId() != null)
            guestListReference = database.getReference().child("Shopping Lists").child(permissionView.getParentList().getListId()).child("guests");

        usersReference = database.getReference().child("Users");
        concreteConnection = database.getReference().child("Connections").child(permissionView.getParentList().getConnectionId());
    }
    @SuppressLint("CheckResult")
    @Override
    public void fetchDialogGuests() {
        guestListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(String.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> getUsersFromDataBase(list));
                }
                guestListReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //avoid
            }
        });
    }
    //TODO:  fix concurrent exception
    @Override
    public void deleteDialogGuests(List<UserInformation> uselessGuests) {
        List<String> usersIds = new ArrayList<>();
        Iterator<UserInformation> it = permissionView.getDialogGuestsList().iterator();
        for(UserInformation fellow : uselessGuests){
            while (it.hasNext()){
                UserInformation guest = it.next();
                if(fellow.equals(guest)) {
                    uselessGuests.remove(fellow);
                    it.remove();
                }
            }
        }

        for(UserInformation user : permissionView.getDialogGuestsList()){
            usersIds.add(user.getId());
        }

        if(permissionView.getDialogGuestsList().size() > 0)
            guestListReference.setValue(usersIds);
        else
            concreteConnection.removeValue();

        permissionView.refreshGuestsList();
        permissionView.activateDeleteButton();
    }

    @Override
    public boolean checkPerson(int position) {
        for(UserInformation uselessUser : permissionView.getUselessGuests()){
                if (uselessUser.getId().equals(permissionView.getDialogGuestsList().get(position).getId()))
                    return true;
        }
        return false;
    }

    @SuppressLint("CheckResult")
    private void getUsersFromDataBase(List<String> usersIds){
        List<UserInformation> dialogUsers = new ArrayList<>();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(UserInformation.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(usersInformation -> {
                                for (String userId : usersIds){
                                    for (UserInformation user : usersInformation){
                                        if(user.getId().equals(userId)) {
                                            dialogUsers.add(user);
                                            break;
                                        }
                                    }
                                }
                                permissionView.getDialogGuestsList().clear();
                                permissionView.getDialogGuestsList().addAll(dialogUsers);
                                permissionView.showDialogGuests();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
