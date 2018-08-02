package ru.startandroid.purchaselist.chat.presenters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import ru.startandroid.purchaselist.chat.view.ChatViewInterface;

/**
 * Created by user on 22/03/2018.
 */

public class ChatPresenterImpl implements ChatPresenter {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private ChatViewInterface chatView;

    public ChatPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, ChatViewInterface chatView){
        this.database = database;
        this.auth = auth;
        this.chatView = chatView;
    }
    @Override
    public void deleteMessage(String messageId) {

    }

    @Override
    public void addMessage(String content) {

    }

    @Override
    public void fetchAllMessages() {

    }
}
