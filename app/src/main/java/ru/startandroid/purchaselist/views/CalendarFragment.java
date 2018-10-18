package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.di.CalendarModule;
import ru.startandroid.purchaselist.presenters.CalendarPresenterInterface;
import ru.startandroid.purchaselist.views.helpers.CalendarAdapter;

/**
 * Created by user on 04/03/2018.
 */

public class CalendarFragment extends Fragment implements CalendarInterface {

    @Inject
    CalendarPresenterInterface presenter;

    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.btnSetToday)
    TextView btnSetToday;
    @BindView(R.id.recyclerCalendarList)
    RecyclerView recyclerCalendarList;

    private List<String> mainList;
    private CalendarAdapter calendarAdapter;
    private SimpleDateFormat sdf;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_view, null);
        ButterKnife.bind(this, v);
        resolveDependencies();
        mainList = new ArrayList<>();
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        calendarAdapter = new CalendarAdapter(mainList);
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
        calendarView.setFirstDayOfWeek(2);
        calendarView.setOnDateChangeListener(getMyDateListener());
        recyclerCalendarList.setLayoutManager(new LinearLayoutManager(getActivity()));
        presenter.fetchListsNames(sdf.format(Calendar.getInstance().getTime()));
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createCalendarComponent(new CalendarModule(this)).inject(this);
    }

    @OnClick(R.id.btnSetToday)
    public void setToday(){
        calendarView.setDate(calendar.getTimeInMillis());
        presenter.fetchListsNames(sdf.format(calendar.getTime()));
    }

    @Override
    public List<String> getList() {
        return mainList;
    }

    @Override
    public void showNames() {
        calendarAdapter.notifyDataSetChanged();
        recyclerCalendarList.setAdapter(calendarAdapter);
    }

    private CalendarView.OnDateChangeListener getMyDateListener(){
        return (view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            String date = sdf.format(c.getTime());
            presenter.fetchListsNames(date);
        };
    }
}
