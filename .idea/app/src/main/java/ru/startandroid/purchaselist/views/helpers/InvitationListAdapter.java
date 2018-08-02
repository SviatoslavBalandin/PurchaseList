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
import ru.startandroid.purchaselist.chat.model.Invitation;
import ru.startandroid.purchaselist.views.InvitationListViewInterface;

/**
 * Created by user on 26/03/2018.
 */

public class InvitationListAdapter extends RecyclerView.Adapter<InvitationListAdapter.ViewHolder>  {

    private String today;
    private String yesterday;
    private SimpleDateFormat sdf;
    private List<Invitation> invitationList;

    public InvitationListAdapter(InvitationListViewInterface invitationListView){
        invitationList = invitationListView.getInvitationList();
    }
    @Override
    public InvitationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_lists_item, parent, false);
        sdf = new SimpleDateFormat("dd.MM.yyyy");
        today = sdf.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        yesterday = sdf.format(calendar.getTime());
        return new ViewHolder(v);
    }
//TODO: finish this adapter custom date
    @Override
    public void onBindViewHolder(InvitationListAdapter.ViewHolder holder, int position) {
        holder.senderName.setText("from " + invitationList.get(position).getSenderName());

        if(invitationList.get(position).getDate().equals(today))
            holder.sendDate.setText(R.string.today);
        else if(invitationList.get(position).getDate().equals(yesterday))
            holder.sendDate.setText(R.string.yesterday);
        else
            holder.sendDate.setText(invitationList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return 0;
    }
 class ViewHolder extends RecyclerView.ViewHolder{
     private TextView senderName;
     private TextView sendDate;
     public ViewHolder(View itemView) {
         super(itemView);
         senderName = (TextView) itemView.findViewById(R.id.inviteSenderNameTv);
         sendDate = (TextView) itemView.findViewById(R.id.dateInInvitation);
     }
 }
}
