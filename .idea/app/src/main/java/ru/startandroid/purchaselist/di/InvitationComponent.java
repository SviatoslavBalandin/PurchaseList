package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.views.InvitationListView;

/**
 * Created by user on 26/03/2018.
 */
@Subcomponent(modules = {InvitationModule.class})
public interface InvitationComponent {

    void inject(InvitationListView view);
}
