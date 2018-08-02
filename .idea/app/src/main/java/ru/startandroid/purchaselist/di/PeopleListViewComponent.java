package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.PeopleView;

/**
 * Created by user on 26/03/2018.
 */
@Subcomponent(modules = {PeopleListViewModule.class})
public interface PeopleListViewComponent {
    void inject(PeopleView peopleView);
}
