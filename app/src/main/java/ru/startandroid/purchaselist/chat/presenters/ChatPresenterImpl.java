package ru.startandroid.purchaselist.chat.presenters;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.chat.view.ChatViewInterface;
import ru.startandroid.purchaselist.presenters.technical_staff.FireFlowableFactory;

/**
 * Created by user on 22/03/2018.
 */

public class ChatPresenterImpl implements ChatPresenter {

    private FirebaseDatabase database;
    private ChatViewInterface chatView;
    private SimpleDateFormat sdf;
    private DatabaseReference connectionReference;
    private String currentUserName;
    private ValueEventListener fetchEventListener;

    private final static String CHAT_KEY = "Chat";

    public ChatPresenterImpl(FirebaseDatabase database, ChatViewInterface chatView){
        this.database = database;
        this.chatView = chatView;
        connectionReference = database.getReference().child("Connections");
        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        currentUserName = chatView.getPrivatePreferences().getString("current user name", "Unknown user");
    }
    @Override
    public void deleteAllMessages() {
        database.getReference().child("Connections").child(chatView.getParentList().getConnectionId()).child("Chat").removeValue();
        chatView.getMessageList().clear();
        chatView.refreshList();
    }

    @Override
    public void addMessage(String content) {
        String time = sdf.format(Calendar.getInstance().getTime());
        String messageKey = connectionReference.child(chatView.getParentList().getConnectionId()).child(CHAT_KEY).push().getKey();
        Message message = new Message(messageKey, content, time, currentUserName);
        connectionReference.child(chatView.getParentList().getConnectionId()).child(CHAT_KEY).child(messageKey).setValue(message);
        if(chatView.getMessageList().size() == 0)
            chatView.getMessageList().add(0, message);
        else
            chatView.getMessageList().add(chatView.getMessageList().size() - 1, message);
        chatView.refreshList();
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchAllMessages() {
        if(!TextUtils.isEmpty(chatView.getParentList().getConnectionId())){
           fetchEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if(dataSnapshot.hasChildren()) {
                       FireFlowableFactory.getFireFlowable(dataSnapshot.getChildren())
                               .map(snapshot -> snapshot.getValue(Message.class))
                               .toList()
                               .subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(messages -> {
                                       chatView.getMessageList().clear();
                                       chatView.getMessageList().addAll(messages);
                                       chatView.refreshList();
                               });
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   //nothing
               }
           };

            connectionReference.child(chatView.getParentList().getConnectionId()).child(CHAT_KEY).addValueEventListener(fetchEventListener);
        }
    }

    @Override
    public void stopListen() {
        try {
            connectionReference.child(chatView.getParentList().getConnectionId()).child(CHAT_KEY).removeEventListener(fetchEventListener);
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
    }
}
