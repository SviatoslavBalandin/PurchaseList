package ru.startandroid.purchaselist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.chat.helpers.PeopleListAdapter;
import ru.startandroid.purchaselist.chat.presenters.PersonListPresenter;
import ru.startandroid.purchaselist.chat.presenters.PersonListPresenterImpl;
import ru.startandroid.purchaselist.chat.view.PeopleViewInterface;

/**
 * Created by user on 26/03/2018.
 */
@Module
public class PeopleListViewModule {

    private final PeopleViewInterface peopleView;

    public PeopleListViewModule(PeopleViewInterface peopleView){
        this.peopleView = peopleView;
    }

    @Provides
    public PersonListPresenter providePersonListPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new PersonListPresenterImpl(database, auth, peopleView);
    }
    @Provides
    public PeopleListAdapter providePeopleListAdapter(){
        return new PeopleListAdapter(peopleView);
    }
}
