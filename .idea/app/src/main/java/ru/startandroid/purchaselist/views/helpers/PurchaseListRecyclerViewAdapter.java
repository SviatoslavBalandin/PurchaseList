package ru.startandroid.purchaselist.views.helpers;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.views.ShoppingListView;

/**
 * Created by user on 18/02/2018.
 */

public class PurchaseListRecyclerViewAdapter extends RecyclerView.Adapter<PurchaseListRecyclerViewAdapter.GoodsListViewHolder> {

    private List<Purchase> goodsList;
    private SharedPreferences preferences;
    private ShoppingListView shoppingListView;

    public PurchaseListRecyclerViewAdapter(ShoppingListView shoppingListView){
        this.shoppingListView = shoppingListView;
        this.preferences = shoppingListView.getMainPreferences();
        this.goodsList = shoppingListView.getGoodsList();
    }

    @Override
    public GoodsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_list_item, null);
        return new GoodsListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GoodsListViewHolder holder, int position) {

        holder.numberAndTitleOfPurchase.setText((position + 1) + ". " + goodsList.get(position).getTitle() );

        String amount = goodsList.get(position).getAmount();

        if(TextUtils.isEmpty(amount))
            holder.quantityOfPurchase.setText("");
        else
            holder.quantityOfPurchase.setText("x" + amount);

        String price = goodsList.get(position).getPrice();
        if(TextUtils.isEmpty(price))
            holder.priseOfPurchase.setText("");
        else
            holder.priseOfPurchase.setText(price);

        holder.purchaseCheckBox.setChecked(goodsList.get(position).isChecked());
        holder.purchaseCheckBox.setOnCheckedChangeListener(initOnCheckedChangeListener(holder));

        checkPrefSize(holder);
        checkPrefMarkStyle(holder);
        checkPrefQuant_PriceVisibility(holder);
    }

    @Override
    public int getItemCount() {
        if(goodsList == null){
            return 0;
        }
        return goodsList.size();
    }

    private void checkPrefSize(GoodsListViewHolder holder){
        String size = preferences.getString("purch_font", "20");
        holder.numberAndTitleOfPurchase.setTextSize(Integer.parseInt(size));
        holder.quantityOfPurchase.setTextSize(Integer.parseInt(size));
        holder.priseOfPurchase.setTextSize(Integer.parseInt(size));
    }
    private void checkPrefMarkStyle(GoodsListViewHolder holder){
        if(preferences.getBoolean("markable", false))
            holder.purchaseCheckBox.setVisibility(View.VISIBLE);
        else
            holder.purchaseCheckBox.setVisibility(View.GONE);
    }
    private void checkPrefQuant_PriceVisibility(GoodsListViewHolder holder){
        if(!preferences.getBoolean("show_quant", false))
            holder.quantityOfPurchase.setVisibility(View.INVISIBLE);
        else
            holder.quantityOfPurchase.setVisibility(View.VISIBLE);
        if(!preferences.getBoolean("show_price", false))
            holder.priseOfPurchase.setVisibility(View.INVISIBLE);
        else
            holder.priseOfPurchase.setVisibility(View.VISIBLE);
    }

    private CompoundButton.OnCheckedChangeListener initOnCheckedChangeListener(GoodsListViewHolder holder){
        return (buttonView, isChecked) ->
            shoppingListView.verifyCheckedItems(isChecked, goodsList.get(holder.getAdapterPosition()).getId());
    }

    class GoodsListViewHolder extends RecyclerView.ViewHolder {

        private TextView numberAndTitleOfPurchase;
        private TextView quantityOfPurchase;
        private TextView priseOfPurchase;
        private CheckBox purchaseCheckBox;

        public GoodsListViewHolder(View itemView) {
            super(itemView);
            numberAndTitleOfPurchase = (TextView) itemView.findViewById(R.id.numberAndTitleOfPurchase);
            quantityOfPurchase = (TextView) itemView.findViewById(R.id.purchaseQuantity);
            priseOfPurchase = (TextView) itemView.findViewById(R.id.purchasePrise);
            purchaseCheckBox = (CheckBox) itemView.findViewById(R.id.purchaseCheckBox);

        }
    }
}
