package ru.startandroid.purchaselist.chat.presenters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.model.Invitation;
import ru.startandroid.purchaselist.chat.view.PeopleViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 26/03/2018.
 */

public class PersonListPresenterImpl implements PersonListPresenter {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private PeopleViewInterface peopleView;
    private DatabaseReference usersReference;
    private static final String USERS_KEY = "Users";
    private  SimpleDateFormat sdf;

    public PersonListPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, PeopleViewInterface peopleView) {
        this.database = database;
        this.auth = auth;
        this.peopleView = peopleView;
        usersReference = database.getReference().child(USERS_KEY);
        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm a");
    }

    @Override
    public void fetchPersons(boolean onCreateView) {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(UserInformation.class))
                            .filter(user -> !user.getId().equals(auth.getCurrentUser().getUid()))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(peopleList -> {
                                peopleView.getStaticPeopleList().addAll(peopleList);
                                peopleView.getPeopleList().addAll(peopleList);
                                peopleView.refreshList();
                            });
                    usersReference.removeEventListener(this);
                } else {
                    usersReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });

    }

    @Override
    public int invitePersons(List<UserInformation> invitedPersons) {
        String INVITATIONS_KEY = "invitations";
        String CURRENT_USER_NAME = "current user name";
        String  time = sdf.format(Calendar.getInstance().getTime());

        if (invitedPersons.size() == 0)
            return 0;
        //handle users
        for (UserInformation user : invitedPersons) {
            //find each user invitations reference
            DatabaseReference invitationReference = database.getReference().child(USERS_KEY).child(user.getId()).child(INVITATIONS_KEY);
            //create invitation key
            String invitationId = invitationReference.push().getKey();
            //create invitation
            Invitation invitation = new Invitation(invitationId, auth.getCurrentUser().getUid(),
                    peopleView.getPreferences().getString(CURRENT_USER_NAME, ""),
                    peopleView.getPreferences().getString("listId", ""),
                    peopleView.getPreferences().getString("listTitle", ""),
                    time);
            //put in this reference our invitation
            invitationReference.child(invitationId).setValue(invitation);

        }
        return invitedPersons.size();
    }

    @Override
    public List<UserInformation> filter(String query) {
        query = query.toLowerCase();
        final List<UserInformation> filteredModeList = new ArrayList<>();
        if (peopleView.getPeopleList().size() != 0) {
            for (UserInformation user : peopleView.getPeopleList()) {
                String lowName = user.getName().toLowerCase();
                if (lowName.startsWith(query)) {
                    filteredModeList.add(user);
                }
            }
        }
        return filteredModeList;
    }

    @Override
    public boolean checkPerson(int position) {
        for (UserInformation info : peopleView.getInvitedPersonsList()){
            if(info.getId().equals(peopleView.getPeopleList().get(position).getId()))
                return true;
        }
        return false;
    }
}