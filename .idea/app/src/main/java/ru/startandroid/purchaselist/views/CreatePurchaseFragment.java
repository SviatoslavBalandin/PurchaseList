package ru.startandroid.purchaselist.views;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenter;

/**
 * Created by user on 18/02/2018.
 */

public class CreatePurchaseFragment extends DialogFragment {

    private ShoppingListPresenter presenter;
    private SharedPreferences preferences;

    @BindView(R.id.btnPurchaseCancel)
    TextView btnCancel;
    @BindView(R.id.btnPurchaseSave)
    TextView btnSave;
    @BindView(R.id.nameOfPurchase)
    EditText nameOfPurchase;
    @BindView(R.id.quantityOfPurchase)
    EditText quantityOfPurchase;
    @BindView(R.id.priceOfPurchase)
    EditText priceOfPurchase;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvQuantity)
    TextView tvQuantity;

    CreatePurchaseFragment(ShoppingListView view, ShoppingListPresenter presenter){
        this.presenter = presenter;
        preferences = view.getMainPreferences();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_purchase_dialog, null);
        ButterKnife.bind(this, v);
        checkPreferences();
        return v;
    }

    @OnClick(R.id.btnPurchaseCancel)
    public void onCancel(){
        dismiss();
    }

    @OnClick(R.id.btnPurchaseSave)
    public void onSave(){
        String name = nameOfPurchase.getText().toString();
        String quantity = quantityOfPurchase.getText().toString();
        String price = priceOfPurchase.getText().toString();
        if(TextUtils.isEmpty(name)){
            nameOfPurchase.setError("This field can't be empty");
            return;
        }else {
            if(quantity.isEmpty())
                quantity = "1";
            if(price.isEmpty())
                price = "0.0";

                presenter.addItem(name, price, quantity);
            dismiss();
        }
    }
    private void checkPreferences(){
        if(!preferences.getBoolean("show_quant", false)) {
            quantityOfPurchase.setVisibility(View.GONE);
            tvQuantity.setVisibility(View.GONE);
        }
        else {
            quantityOfPurchase.setVisibility(View.VISIBLE);
            tvQuantity.setVisibility(View.VISIBLE);
        }

        if(!preferences.getBoolean("show_price", false)) {
            priceOfPurchase.setVisibility(View.GONE);
            tvPrice.setVisibility(View.GONE);
        }
        else {
            priceOfPurchase.setVisibility(View.VISIBLE);
            tvPrice.setVisibility(View.VISIBLE);
        }

    }
}
