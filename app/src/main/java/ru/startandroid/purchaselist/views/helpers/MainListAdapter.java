package ru.startandroid.purchaselist.views.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.views.AccountScreenView;

/**
 * Created by user on 25/12/2017.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainListViewHolder> {


    private List<GoodsList> goodsLists;
    private String today;
    private String yesterday;
    private AccountScreenView accountScreenView;

    public MainListAdapter(AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        goodsLists = accountScreenView.getMainList();
    }

    @Override
    public MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_lists_item, parent, false);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
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
        return goodsLists == null ? 0 : goodsLists.size();
    }

 class MainListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemTitle)
        TextView itemTitle;

        @BindView(R.id.date)
        TextView itemDate;

        @BindView(R.id.quantityOnListTv)
        TextView quantityOnListTv;

        public MainListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemDate = (TextView) itemView.findViewById(R.id.date);
            quantityOnListTv = (TextView) itemView.findViewById(R.id.quantityOnListTv);
        }
    }

}
