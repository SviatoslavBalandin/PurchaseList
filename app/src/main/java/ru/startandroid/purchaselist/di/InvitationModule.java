package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.presenters.InvitationListPresenter;
import ru.startandroid.purchaselist.presenters.InvitationListPresenterImpl;
import ru.startandroid.purchaselist.views.InvitationListViewInterface;

/**
 * Created by user on 26/03/2018.
 */
@Module
public class InvitationModule {

    private final InvitationListViewInterface invitationListView;

    public InvitationModule(InvitationListViewInterface invitationListView){
        this.invitationListView = invitationListView;
    }
    @Provides
    public InvitationListPresenter provideInvitationListPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new InvitationListPresenterImpl(database, auth, invitationListView);
    }

}
