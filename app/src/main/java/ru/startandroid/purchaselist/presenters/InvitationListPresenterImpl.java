package ru.startandroid.purchaselist.presenters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.model.Answer;
import ru.startandroid.purchaselist.chat.model.Invitation;
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.views.InvitationListViewInterface;

/**
 * Created by user on 26/03/2018.
 */

public class InvitationListPresenterImpl implements InvitationListPresenter {
    private FirebaseDatabase database;
    private  FirebaseAuth auth;
    private InvitationListViewInterface invitationListView;
    private DatabaseReference invitationsReference;

    private final String USERS_KEY = "Users";

public InvitationListPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, InvitationListViewInterface invitationListView){
    this.database = database;
    this.auth = auth;
    this.invitationListView = invitationListView;
    invitationsReference = database.getReference().child(USERS_KEY).child(auth.getCurrentUser().getUid()).child("invitations");
}
    @Override
    public void fetchInvitation() {
        invitationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(Invitation.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(invitations -> {
                                invitationListView.getInvitationList().clear();
                                invitationListView.getInvitationList().addAll(invitations);
                                invitationListView.showInvitation();
                                invitationListView.refreshInvitationList();
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });
    }

    @Override
    public void sendAnswer(Invitation invitation, boolean answer) {
        DatabaseReference answerReference = database.getReference().child(USERS_KEY).child(invitation.getSenderId()).child("answers");
        String answerId = answerReference.push().getKey();
        Answer ans = new Answer(answerId, invitation.getListId(), auth.getCurrentUser().getUid(), answer);
        answerReference.child(answerId).setValue(ans);
    }

    @Override
    public void deleteInvitation(Invitation invitation) {
        invitationsReference.child(invitation.getId()).removeValue();
        invitationListView.getInvitationList().remove(invitation);
        invitationListView.refreshInvitationList();
    }

    @Override
    public void deleteAllInvitations() {
        for (Invitation invitation : invitationListView.getInvitationList()){
            sendAnswer(invitation, false);
        }
        invitationsReference.removeValue();
        invitationListView.getInvitationList().clear();
        invitationListView.refreshInvitationList();
    }

}
