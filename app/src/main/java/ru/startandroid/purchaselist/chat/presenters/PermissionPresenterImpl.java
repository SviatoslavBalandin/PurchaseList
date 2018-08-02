package ru.startandroid.purchaselist.chat.presenters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.view.PermissionViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 10/04/2018.
 */

public class PermissionPresenterImpl implements PermissionPresenter{

    private PermissionViewInterface permissionView;
    private DatabaseReference concreteConnection;
    private DatabaseReference guestListReference;
    private DatabaseReference usersReference;

    public PermissionPresenterImpl(PermissionViewInterface permissionView, FirebaseDatabase database){
        this.permissionView = permissionView;
        concreteConnection = database.getReference().child("Connections").child(permissionView.getConnectionId());
        if(permissionView.getConnectionId() != null)
            guestListReference = database.getReference().child("Connections").child(permissionView.getConnectionId()).child("guestsList");
        usersReference = database.getReference().child("Users");
    }

    @Override
    public void fetchDialogGuests() {
        guestListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
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

    @Override
    public void deleteDialogGuests(List<UserInformation> fellows) {
        List<String> usersIds = new ArrayList<>();
        for(UserInformation fellow : fellows){
            for(UserInformation guest : permissionView.getDialogGuestsList()){
                if(fellow.equals(guest)) {
                    permissionView.getDialogGuestsList().remove(guest);
                    fellows.remove(fellow);
                }
            }
        }
        for(UserInformation user : permissionView.getDialogGuestsList()){
            usersIds.add(user.getId());
        }
        if(usersIds.size() > 0)
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

    private void getUsersFromDataBase(List<String> usersIds){
        List<UserInformation> dialogUsers = new ArrayList<>();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(UserInformation.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userInformations -> {
                                for (String userId : usersIds){
                                    for (UserInformation user : userInformations){
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