package ru.startandroid.purchaselist.di;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerFragment;
import ru.startandroid.purchaselist.presenters.CalendarPresenter;
import ru.startandroid.purchaselist.presenters.CalendarPresenterInterface;
import ru.startandroid.purchaselist.views.CalendarInterface;

@Module
public class CalendarModule {

    private final CalendarInterface calendarView;

    public CalendarModule(CalendarInterface calendarView) {
        this.calendarView = calendarView;
    }

    @PerFragment
    @Provides
    public CalendarPresenterInterface getCalendarPresenter(FirebaseDatabase database, FirebaseAuth auth){
        return new CalendarPresenter(database, auth, calendarView);
    }
}
