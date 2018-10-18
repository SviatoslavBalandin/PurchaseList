package ru.startandroid.purchaselist.views.helpers;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder>  {

    private List<String> listsNames;

    public CalendarAdapter(List<String> listsNames) {
        this.listsNames = listsNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.listName.setPaintFlags(holder.listName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.listName.setText(listsNames.get(position));
    }

    @Override
    public int getItemCount() {
        return listsNames.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCalendarListName)
        TextView listName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
