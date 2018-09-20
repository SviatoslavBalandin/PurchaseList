package ru.startandroid.purchaselist.presenters;

import android.annotation.SuppressLint;
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.model.Answer;
import ru.startandroid.purchaselist.chat.model.Connection;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.presenters.technical_staff.DateComparator;
import ru.startandroid.purchaselist.presenters.technical_staff.FireFlowableFactory;
import ru.startandroid.purchaselist.views.AccountScreenView;

/**
 * Created by user on 19/11/2017.
 */

public class AccountPresenterImpl implements AccountPresenter{

    private AccountScreenView accountScreenView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference shoppingListsReference;
    private DatabaseReference answerReference;
    private DatabaseReference connectionReference;
    private String today;
    private String currentUserId;
    private ValueEventListener fetchListsEventListener;
    private ValueEventListener answerListener;

    private final String SHOPPING_LISTS_KEY = "Shopping Lists";

    @Inject
    public AccountPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        this.database = database;
        firebaseAuth = auth;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        shoppingListsReference = database.getReference().child(SHOPPING_LISTS_KEY);
        answerReference = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child( "answers");
        connectionReference = database.getReference().child("Connections");
        today = sdf.format(Calendar.getInstance().getTime());
        currentUserId = auth.getCurrentUser().getUid();
    }
    @Override
    public void addList(){
        String goodsListReference =  shoppingListsReference.push().getKey();
        GoodsList newList = new GoodsList("New List", today, goodsListReference,
                new UserInformation(firebaseAuth.getCurrentUser().getEmail(),
                        accountScreenView.getPrivatePreferences().getString("current user name", "Unknown User"),
                        currentUserId), true);
        shoppingListsReference.child(goodsListReference).setValue(newList);
        accountScreenView.getMainList().add(0, newList);
    }

    @Override
    public void deleteList() {
       performDeleting(0);
    }
    @Override
    public void deleteList(int position) {
        performDeleting(position);
    }

    @Override
    public void renameList(String newName, String listId) {
        DatabaseReference listNameReference = database.getReference("Shopping Lists").child(listId).child("title");
        listNameReference.setValue(newName);

    }

    @Override
    public void fetchLists() {
        fetchListsEventListener = new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                                .map(dataSnapshot1 -> dataSnapshot1.getValue(GoodsList.class))
                                .filter(list -> list != null)
                                .filter(list -> list.getOwner().getId().equals(currentUserId) || checkPresentGuest(currentUserId, list.getGuests()))
                                .toList()
                                .map(list -> flipList(list))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        list -> addingFinished(list));
                    }
                }catch (Exception e){
                    //
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        };

        shoppingListsReference.addValueEventListener(fetchListsEventListener);
    }
    @Override
    public void reactOnAnswer() {
        answerListener = new ValueEventListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                                .map(dataSnapshot1 -> dataSnapshot1.getValue(Answer.class))
                                .filter(Answer::getContent)
                                .toList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(list -> {
                                    Log.e("myLog", "size: " + String.valueOf(list.size()));
                                    addGuestsToList(list);
                                });
                    }
                }catch (Exception e){
                    //skip
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        };
        answerReference.addValueEventListener(answerListener);

    }

    @Override
    public void removeUnusedAnswers() {
        answerReference.removeValue();
    }

    @Override
    public void stopListen() {
        try {
            shoppingListsReference.removeEventListener(fetchListsEventListener);
            answerReference.removeEventListener(answerListener);
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
    }

    private void addingFinished(List<GoodsList> mainList){
        switchOwners(mainList);
        accountScreenView.getMainList().clear();
        accountScreenView.getMainList().addAll(0, mainList);
        accountScreenView.showProducts();
        accountScreenView.refreshList();
    }
    private List<GoodsList> flipList(List<GoodsList> oldList){
        List<GoodsList> newList = new ArrayList<>();
        for(int i = oldList.size() - 1; i >= 0; i-- ){
            newList.add(oldList.get(i));
        }
        Collections.sort(newList, new DateComparator());

        return newList;
    }
    private void switchOwners(List<GoodsList>  mainList){
        for (GoodsList list : mainList) {
            if(!currentUserId.equals(list.getOwner().getId())){
                list.isOwner = false;
            }
        }
    }
    //TODO: fix loop rush and spamming by connections and in guest list
    private void addGuestsToList(List<Answer> list){
        int guestsLimit = 5;
        for (Answer answer : list){
            shoppingListsReference.child(answer.getListId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GoodsList goodsList = dataSnapshot.getValue(GoodsList.class);
                    try {
                        Connection connection = null;
                        Log.e("myLog", "first");
                        Log.e("myLog", "" + checkPresentGuest(answer.getUserId(), goodsList.getGuests()) + ", size = " + goodsList.getGuests().size() );
                        if(!checkPresentGuest(answer.getUserId(), goodsList.getGuests()) && goodsList.getGuests().size() < guestsLimit){
                            Log.e("myLog", "inside of if");
                            int size = goodsList.getGuests().size();
                            Log.e("myLog", "guests size received");
                            goodsList.getGuests().add(answer.getUserId());
                            Log.e("myLog", "size = " + size + ", goodsList.getGuests().size() = " + goodsList.getGuests().size());
                            if(size == 0) {
                                Log.e("myLog", "size = 0");
                                //connection = uploadConnection(answer);
                                Log.e("myLog", "upload Connection");
                            }else if(goodsList.getGuests().size() > 1 && goodsList.getGuests().size() < 5){
                                //connection.getGuestsList().add(answer.getUserId());
                                Log.e("myLog", "add friend 2");
                                //connectionReference.child(connection.getId()).setValue(connection);
                                Log.e("myLog", "add friend to Connection");
                            }
                        }
                    }catch (NullPointerException e){
                            Log.e("myLog", "NullPointerException in addGuestsToList method");
                    }
                    Log.e("myLog", "after if");
                    database.getReference().child(SHOPPING_LISTS_KEY).child(answer.getListId()).setValue(goodsList);

                    answerReference.child(answer.getAnswerId()).removeValue();
                    shoppingListsReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    shoppingListsReference.removeEventListener(this);
                }

            });
        }
    }

    private boolean checkPresentGuest(String id, List<String> guestsId){
        try {
            for (String currentId : guestsId) {
                if (currentId.equals(id))
                    return true;
            }
        }catch (NullPointerException e) {
            //skip
        }
        return false;
    }
    private Connection uploadConnection(Answer answer) {
        String id = connectionReference.push().getKey();
        Connection connection = new Connection(id, currentUserId, answer.getListId(), answer.getUserId());
        connectionReference.child(id).setValue(connection);
        shoppingListsReference.child(answer.getListId()).child("connectionId").setValue(id);
        return connection;
    }
    private boolean checkPresentConnection(Connection connection, List<Connection> connectionList){
        for(Connection certainConnect : connectionList){
            if(certainConnect.getListId().equals(connection.getListId()))
                return true;
        }
        return false;
    }
    private void performDeleting(int position){

        if (accountScreenView.getMainList().size() == 0)
            return;

        GoodsList trashList = accountScreenView.getMainList().get(position);

        if(trashList.isOwner) {
            try {
                if(!trashList.getConnectionId().equals(""))
                    connectionReference.child(trashList.getConnectionId()).removeValue();

                shoppingListsReference.child(trashList.getListId()).removeValue();
            }catch (NullPointerException e){
                e.fillInStackTrace();
            }
        }
        else {
            for (String guestId : trashList.getGuests()) {
                if(currentUserId.equals(guestId)) {
                    trashList.getGuests().remove(guestId);
                    shoppingListsReference.child(trashList.getListId()).setValue(trashList);
                }
            }
            connectionReference.child(trashList.getConnectionId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Connection connection = dataSnapshot.getValue(Connection.class);

                        for(String guestId : connection.getGuestsList()){
                            if(guestId.equals(currentUserId)){
                                connection.getGuestsList().remove(guestId);
                                connectionReference.child(trashList.getConnectionId()).setValue(connection);
                                if(connection.getGuestsList().size() == 0){
                                    connectionReference.child(connection.getId()).removeValue();
                                }
                                break;
                            }
                        }

                    }catch (NullPointerException e){
                        e.fillInStackTrace();
                    }
                    connectionReference.child(trashList.getConnectionId()).removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //nothing
                }
            });
        }
        accountScreenView.getMainList().remove(position);
        accountScreenView.refreshList();
    }
}

