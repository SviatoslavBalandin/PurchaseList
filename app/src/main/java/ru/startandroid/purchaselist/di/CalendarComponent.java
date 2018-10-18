package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.di.annotations.PerFragment;
import ru.startandroid.purchaselist.views.CalendarFragment;

@PerFragment
@Subcomponent(modules = {CalendarModule.class})
public interface CalendarComponent {
    void inject(CalendarFragment calendarFragment);
}
