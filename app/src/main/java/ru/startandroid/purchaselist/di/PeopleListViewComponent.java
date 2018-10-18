package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.chat.view.PeopleView;
import ru.startandroid.purchaselist.di.annotations.PerFragment;

/**
 * Created by user on 26/03/2018.
 */
@PerFragment
@Subcomponent(modules = {PeopleListViewModule.class})
public interface PeopleListViewComponent {
    void inject(PeopleView peopleView);
}
