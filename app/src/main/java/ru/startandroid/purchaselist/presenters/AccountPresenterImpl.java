package ru.startandroid.purchaselist.presenters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
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
import java.util.Locale;

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


public class AccountPresenterImpl implements AccountPresenter{

    private AccountScreenView accountScreenView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference shoppingListsReference;
    private DatabaseReference answerReference;
    private DatabaseReference connectionReference;
    private String today;
    private String currentUserId;
    private ValueEventListener fetchListsEventListener;
    private ValueEventListener answerListener;
    private  List<Connection> connectionBuffer;
    private List<GoodsList> goodsListsBuffer;

    @Inject
    public AccountPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        firebaseAuth = auth;
        try {
            currentUserId = auth.getCurrentUser().getUid();
        }catch (Exception e){
            if(currentUserId == null)
                currentUserId = "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        shoppingListsReference = database.getReference().child("Shopping Lists");
        answerReference = database.getReference().child("Users").child(currentUserId).child( "answers");
        connectionReference = database.getReference().child("Connections");
        today = sdf.format(Calendar.getInstance().getTime());
        connectionBuffer = new ArrayList<>();
        goodsListsBuffer = new ArrayList<>();
    }
    @Override
    public void addList(){
        Log.e("LOg", "add list");
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
        DatabaseReference listNameReference = shoppingListsReference.child(listId).child("title");
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
        Log.e("LOg", "react on answer");
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
                                .subscribe(list -> addGuestsToList(list));


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
    private void addGuestsToList(List<Answer> answers){
        int guestsLimit = 5;
        for (Answer answer : answers) {
            shoppingListsReference.child(answer.getListId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GoodsList goodsList = dataSnapshot.getValue(GoodsList.class);
                    goodsList = checkListInBuffer(goodsList);
                    if (goodsList.getGuests() == null)
                        goodsList.setGuests(new ArrayList<>());
                    if (!checkPresentGuest(answer.getUserId(), goodsList.getGuests()) && goodsList.getGuests().size() < guestsLimit) {
                        goodsList.getGuests().add(answer.getUserId());
                        String connectionId = checkConnectionByListId(connectionBuffer, goodsList.getListId());
                        if (!goodsList.getConnectionId().equals("")){
                            connectionReference.child(goodsList.getConnectionId()).child("guestsList").setValue(goodsList.getGuests());
                        } else if (!connectionId.equals("!")) {
                            connectionReference.child(connectionId).child("guestsList").setValue(goodsList.getGuests());
                        }else {
                            uploadConnection(goodsList, answer);
                        }
                        shoppingListsReference.child(answer.getListId()).setValue(goodsList);
                    }
                    answerReference.child(answer.getAnswerId()).removeValue();
                    shoppingListsReference.child(answer.getListId()).removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    shoppingListsReference.removeEventListener(this);
                }
            });
        }
    }
    private GoodsList checkListInBuffer(GoodsList goodsList) {
        if(goodsListsBuffer.size() == 0){
            goodsListsBuffer.add(goodsList);
            return goodsList;
        }
        for (GoodsList previousList : goodsListsBuffer) {
            if(previousList.getListId().equals(goodsList.getListId()))
                return previousList;
        }
        goodsListsBuffer.add(goodsList);
        return goodsList;
    }
    private String checkConnectionByListId(List<Connection> connections, String listId){
        String notFound = "!";

        if(listId.equals("") || connections.size() == 0)
            return notFound;
        for (Connection connection : connections){
            if(connection.getListId().equals(listId))
                return connection.getId();
        }
        return notFound;
    }
    private boolean checkPresentGuest(String id, List<String> guestsId){
        try {
            for (String currentId : guestsId) {
                if (currentId.equals(id)) {
                    return true;
                }
            }
        }catch (NullPointerException e) {
            //skip
        }
        return false;
    }
    private void uploadConnection(@NonNull GoodsList goodsList, Answer answer) {
        if(goodsList.getConnectionId().equals("")) {
            String id = connectionReference.push().getKey();
            goodsList.setConnectionId(id);
            Connection connection = new Connection(id, currentUserId, goodsList.getListId(), answer.getUserId());
            shoppingListsReference.child(goodsList.getListId()).child("connectionId").setValue(id);
            connectionReference.child(id).setValue(connection);
            connectionBuffer.add(connection);
        }
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

