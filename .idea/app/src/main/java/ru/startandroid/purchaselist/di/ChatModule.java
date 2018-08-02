package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenter;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenterImpl;
import ru.startandroid.purchaselist.chat.view.ChatViewInterface;

/**
 * Created by user on 22/03/2018.
 */
@Module
public class ChatModule {

    private final ChatViewInterface chatView;

    public ChatModule(ChatViewInterface chatView){
        this.chatView = chatView;
    }
    @Provides
    ChatPresenter provideChatPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new ChatPresenterImpl(database, auth, chatView);
    }
}
