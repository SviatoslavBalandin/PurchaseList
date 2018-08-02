package ru.startandroid.purchaselist.views.helpers;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;

/**
 * Created by user on 12/04/2018.
 */

public class InfoTotal extends DialogFragment {

    @BindView(R.id.infoTotalAmount)
    TextView amount;

    private StringBuilder totalContainer;

    public InfoTotal(){}

    @SuppressLint("ValidFragment")
    public InfoTotal(String total){
        totalContainer = new StringBuilder();
        totalContainer.append(total);
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_total, null);
        ButterKnife.bind(this, view);
        amount.setText(totalContainer.toString());
        return view;
    }
    public void setAmount(String total){
        if(!TextUtils.isEmpty(total)) {
            totalContainer.delete(0, totalContainer.length());
            totalContainer.append(total);
            amount.setText(totalContainer.toString());
        }
    }
}
