package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.ChatView;

/**
 * Created by user on 22/03/2018.
 */
@Subcomponent(modules = {ChatModule.class})
public interface ChatComponent {
    void inject(ChatView view);
}
