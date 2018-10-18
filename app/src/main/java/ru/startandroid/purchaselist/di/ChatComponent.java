package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.ChatView;
import ru.startandroid.purchaselist.di.annotations.PerFragment;

/**
 * Created by user on 22/03/2018.
 */
@PerFragment
@Subcomponent(modules = {ChatModule.class})
public interface ChatComponent {
    void inject(ChatView view);
}
