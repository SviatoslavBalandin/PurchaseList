package ru.startandroid.purchaselist.presenters;

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

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.chat.model.Answer;
import ru.startandroid.purchaselist.chat.model.Connection;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.UserInformation;
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
    private  String currentUserId;
    private ValueEventListener addForeignListEventListener;
    private ValueEventListener fetchListsEventListener;
    private ValueEventListener answerListener;

    private final String SHOPPING_LISTS_KEY = "Shopping Lists";
    private final String CONNECTIONS_KEY = "Connections";


    @Inject
    public AccountPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        this.database = database;
        firebaseAuth = auth;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        shoppingListsReference = database.getReference().child(SHOPPING_LISTS_KEY);
        answerReference = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child( "answers");
        connectionReference = database.getReference().child(CONNECTIONS_KEY);
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
        if(accountScreenView.getMainList().size() == 0)
            return;
        GoodsList trashList = accountScreenView.getMainList().get(0);
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
            connectionReference.child(trashList.getConnectionId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Observable.just(dataSnapshot.getValue(Connection.class))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(connection -> {
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
                                });
                    }catch (NullPointerException e){
                        e.fillInStackTrace();
                    }
                    connectionReference.child(trashList.getConnectionId()).removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //nothing to show
                }
            });
        }
        accountScreenView.getMainList().remove(0);
        accountScreenView.refreshList();
    }
    @Override
    public void deleteList(int position) {
        if(accountScreenView.getMainList().size() == 0)
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
            connectionReference.child(trashList.getConnectionId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Observable.just(dataSnapshot.getValue(Connection.class))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(connection -> {
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
                                });
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

    @Override
    public void renameList(String newName, String listId) {
        DatabaseReference listNameReference = database.getReference("Shopping Lists").child(listId).child("title");
        listNameReference.setValue(newName);

    }

    @Override
    public void fetchLists() {
      fetchListsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        Observable.fromIterable(dataSnapshot.getChildren())
                                .map(dataSnapshot1 -> dataSnapshot1.getValue(GoodsList.class))
                                .filter(list -> list != null)
                                .filter(list -> list.getOwner().getId().equals(currentUserId))
                                .toList()
                                .map(list -> flipList(list))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(list -> {
                                    accountScreenView.getMainList().clear();
                                    accountScreenView.getMainList().addAll(0, list);
                                    checkAndAddList(accountScreenView.getMainList());
                                    accountScreenView.showProducts();
                                });
                    }
                }catch (Exception e){
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        };

        shoppingListsReference.addValueEventListener(fetchListsEventListener);
    }
    private void checkAndAddList(List<GoodsList> goodsLists){
     addForeignListEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
                            .map(dataSnapshot1 -> dataSnapshot1.getValue(Connection.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(connections ->{
                                for(Connection connection : connections){
                                    for (String userId : connection.getGuestsList()){
                                        if(userId.equals(currentUserId)){
                                            shoppingListsReference.child(connection.getListId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {
                                                        GoodsList goodsList = dataSnapshot.getValue(GoodsList.class);
                                                        goodsList.isOwner = false;
                                                        boolean alreadyPresent = false;
                                                        for(GoodsList gl : goodsLists){
                                                            if(gl.getListId().equals(goodsList.getListId())) {
                                                                goodsLists.set(goodsLists.indexOf(gl), goodsList);
                                                                alreadyPresent = true;
                                                            }
                                                        }
                                                        if(!alreadyPresent)
                                                            goodsLists.add(0, goodsList);

                                                        accountScreenView.refreshList();
                                                        shoppingListsReference.child(connection.getListId()).removeEventListener(this);
                                                    }catch (NullPointerException e){
                                                        return;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        };

        connectionReference.addValueEventListener(addForeignListEventListener);
    }
    private List<GoodsList> flipList(List<GoodsList> oldList){
        List<GoodsList> newList = new ArrayList<>();
        for(int i = oldList.size() - 1; i >= 0; i-- ){
            newList.add(oldList.get(i));
        }
        return newList;
    }

    @Override
    public void reactOnAnswer() {
        answerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        Observable.fromIterable(dataSnapshot.getChildren())
                                .map(dataSnapshot1 -> dataSnapshot1.getValue(Answer.class))
                                .filter(Answer::getContent)
                                .toList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(list -> createConnections(list));
                    }
                }catch (Exception e){
                    //nothing
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //nothing
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
            connectionReference.removeEventListener(addForeignListEventListener);
            shoppingListsReference.removeEventListener(fetchListsEventListener);
            answerReference.removeEventListener(answerListener);
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
    }

    private void createConnections(List<Answer> answerList){
        connectionReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Connection> connectionList = new ArrayList<>();
                boolean connectionsStorageAreEmpty = true;

                if(dataSnapshot.hasChildren())
                    connectionsStorageAreEmpty = false;

                for(Answer answer : answerList){
                    if(connectionsStorageAreEmpty){
                        connectionList.add(uploadConnection(answer));
                        connectionsStorageAreEmpty = false;
                    }else {
                        Observable.fromIterable(dataSnapshot.getChildren())
                                .map(dataSnapshot1 -> dataSnapshot1.getValue(Connection.class))
                                .toList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(list -> {
                                    boolean doesAlreadyExist = false;
                                    connectionList.addAll(list);
                                    for(Connection connection : connectionList){
                                        if(connection.getListId().equals(answer.getListId()) &&
                                                checkPresentConnection(connection, connectionList)){
                                            doesAlreadyExist = true;
                                            if(connection.getGuestsList().size() <= 4){
                                                boolean doesAlreadyPresent = false;
                                                for(String guestId : connection.getGuestsList()){
                                                    if(guestId.equals(answer.getUserId()))
                                                        doesAlreadyPresent = true;
                                                }
                                                if(!doesAlreadyPresent) {
                                                    connection.getGuestsList().add(answer.getUserId());
                                                    connectionReference.child(connection.getId()).setValue(connection);
                                                    return;
                                                }
                                            }else{
                                                return;
                                            }
                                        }
                                    }
                                    if(!doesAlreadyExist) {
                                        connectionList.add(uploadConnection(answer));
                                    }
                                });
                    }
                }
                connectionReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });
    }
    private Connection uploadConnection(Answer answer) {
        String id = connectionReference.push().getKey();
        Connection connection = new Connection(id, currentUserId, answer.getListId(), answer.getUserId());
        database.getReference().child(CONNECTIONS_KEY).child(id).setValue(connection);
        database.getReference().child(SHOPPING_LISTS_KEY).child(answer.getListId()).child("connectionId").setValue(id);
        return connection;
    }
    private boolean checkPresentConnection(Connection connection, List<Connection> connectionList){
        for(Connection certainConnect : connectionList){
            if(certainConnect.getListId().equals(connection.getListId()))
                return true;
        }
        return false;
    }
}

