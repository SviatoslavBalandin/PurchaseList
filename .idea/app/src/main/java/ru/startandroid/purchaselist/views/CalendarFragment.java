package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;

/**
 * Created by user on 04/03/2018.
 */

public class CalendarFragment extends Fragment {

    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.tvSetToday)
    TextView tvSetToday;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_view, null);
        ButterKnife.bind(this, v);
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
        calendarView.setFirstDayOfWeek(2);
        tvSetToday.setPaintFlags(tvSetToday.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return v;
    }

    @OnClick(R.id.tvSetToday)
    public void setToday(){
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
    }

}
