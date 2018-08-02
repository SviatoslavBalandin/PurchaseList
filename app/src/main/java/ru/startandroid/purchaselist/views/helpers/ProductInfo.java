package ru.startandroid.purchaselist.views.helpers;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.model.Purchase;

/**
 * Created by user on 12/04/2018.
 */

public class ProductInfo  extends DialogFragment{

    @BindView(R.id.infoProdNameOfPurchase)
    TextView prodName;
    @BindView(R.id.infoProdQuantityOfPurchase)
    TextView prodQuantity;
    @BindView(R.id.infoProdPriceOfPurchase)
    TextView prodPrice;

    private Purchase product;

    public ProductInfo(){}

    @SuppressLint("ValidFragment")
    public ProductInfo(Purchase product){
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        this.product = product;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_product, null);
        ButterKnife.bind(this, view);
        prodName.setText(product.getTitle());
        prodQuantity.setText(product.getAmount());
        prodPrice.setText(product.getPrice());
        return view;
    }
    @OnClick(R.id.infoProdDismiss)
    public void hitOnDismiss(){
        dismiss();
    }
}
