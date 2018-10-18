package ru.startandroid.purchaselist.di;

import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenter;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenterImpl;
import ru.startandroid.purchaselist.chat.view.ChatViewInterface;
import ru.startandroid.purchaselist.di.annotations.PerFragment;

/**
 * Created by user on 22/03/2018.
 */
@Module
public class ChatModule {

    private final ChatViewInterface chatView;

    public ChatModule(ChatViewInterface chatView){
        this.chatView = chatView;
    }

    @PerFragment
    @Provides
    ChatPresenter provideChatPresenter(FirebaseDatabase database){
        return new ChatPresenterImpl(database, chatView);
    }
}
