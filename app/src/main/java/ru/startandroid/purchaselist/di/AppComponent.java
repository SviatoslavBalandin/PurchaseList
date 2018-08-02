package ru.startandroid.purchaselist.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.startandroid.purchaselist.di.fireBaseModule.FirebaseModule;

@Singleton
@Component(modules = {FirebaseModule.class})
public interface AppComponent {

    AccountMainListComponent createUserComponent(AccountMainListModule mainListModule);

    MainComponent createMainComponent(MainActivityModule module);

    AuthComponent createAuthComponent(AuthModule authModule);

    GoodsListComponent createGoodsListComponent(GoodsListModule goodsListModule);

    ChatComponent createChatComponent(ChatModule chatModule);

    PeopleListViewComponent createPeopleListViewComponent(PeopleListViewModule peopleModule);

    InvitationComponent createInvitationComponent(InvitationModule invitationModule);

    PermissionsComponent createPermissionsComponent(PermissionsModule permissionsModule);

}

