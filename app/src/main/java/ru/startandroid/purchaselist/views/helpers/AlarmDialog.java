package ru.startandroid.purchaselist.views.helpers;



import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;

/**
 * Created by user on 30/03/2018.
 */

public class AlarmDialog extends DialogFragment {

    @BindView(R.id.alarmDialogTitle)
    TextView alarmDialogTitle;
    @BindView(R.id.alarmDialogButtonNegative)
    Button negativeButton;
    @BindView(R.id.alarmDialogButtonPositive)
    Button positiveButton;

    private int titleText = 0;
    private int negativeBtnText = 0;
    private int positiveBtnText = 0;
    private String[] titleTextArray;

    private AlarmOnClickListener listener;
    private int dialogId;

    public AlarmDialog(){}

    @SuppressLint("ValidFragment")
    public AlarmDialog(AlarmOnClickListener listener, int dialogId){
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        this.listener = listener;
        this.dialogId = dialogId;
        titleTextArray = new String[1];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_dialog, null);
        ButterKnife.bind(this, view);
        checkParameters();

        if(titleTextArray[0] != null){
            alarmDialogTitle.setText(titleTextArray[0]);
        }else
            alarmDialogTitle.setText(titleText);

        negativeButton.setText(negativeBtnText);
        positiveButton.setText(positiveBtnText);
        return view;
    }

    @OnClick(R.id.alarmDialogButtonNegative)
    public void onNegativeClick(){
        dismiss();
    }

    @OnClick(R.id.alarmDialogButtonPositive)
    public void onPositiveClick(){
        listener.onPositiveClick(dialogId);
        dismiss();
    }
    public void setAlarmDialogTitle(String text){
        titleTextArray[0] = text;
    }
    public void setAlarmDialogTitle(int textResource){titleText = textResource;}
    public void setNegativeButton(int textResource){negativeBtnText = textResource;}
    public void setPositiveButton(int textResource){
        positiveBtnText = textResource;
    }

    private void checkParameters(){
        if(titleText == 0)
            titleText = R.string.alarm_default_title;
        if(negativeBtnText == 0)
            negativeBtnText = R.string.no;
        if(positiveBtnText == 0)
            positiveBtnText = R.string.yes;

    }
}
