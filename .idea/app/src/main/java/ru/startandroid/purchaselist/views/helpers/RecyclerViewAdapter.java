package ru.startandroid.purchaselist.views.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.views.AccountScreenView;

/**
 * Created by user on 25/12/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MainListViewHolder> {


    private List<GoodsList> goodsLists;
    private String today;
    private String yesterday;
    private SimpleDateFormat sdf;
    private AccountScreenView accountScreenView;

    public RecyclerViewAdapter(AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        goodsLists = accountScreenView.getMainList();
    }

    @Override
    public MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_lists_item, parent, false);
        sdf = new SimpleDateFormat("dd.MM.yyyy");
        today = sdf.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        yesterday = sdf.format(calendar.getTime());
        return new MainListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainListViewHolder holder, int position) {

        holder.itemTitle.setText(goodsLists.get(position).getTitle());

        if(goodsLists.get(position).getDate().equals(today))
            holder.itemDate.setText(R.string.today);
        else if(goodsLists.get(position).getDate().equals(yesterday))
            holder.itemDate.setText(R.string.yesterday);
        else
            holder.itemDate.setText(goodsLists.get(position).getDate());

        holder.itemTitle.setTextSize(Integer.parseInt(accountScreenView.getMainPreferences().getString("list_font", "28")));

        holder.quantityOnListTv.setText("(" + String.valueOf(goodsLists.get(position).getProductsAmount()) + ")");

        if(accountScreenView.getMainPreferences().getBoolean("lists_show_quantity", false)){
            holder.quantityOnListTv.setVisibility(View.VISIBLE);
        }else
            holder.quantityOnListTv.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        if(goodsLists == null)
            return 0;
        return goodsLists.size();
    }

 class MainListViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;
        private TextView itemDate;
        private TextView quantityOnListTv;

        public MainListViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemDate = (TextView) itemView.findViewById(R.id.date);
            quantityOnListTv = (TextView) itemView.findViewById(R.id.quantityOnListTv);
        }
    }

}
