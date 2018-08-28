package ru.startandroid.purchaselist.di;

import dagger.Component;
import ru.startandroid.purchaselist.di.annotations.PerApp;
import ru.startandroid.purchaselist.di.fireBaseModule.FirebaseModule;

@PerApp
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

