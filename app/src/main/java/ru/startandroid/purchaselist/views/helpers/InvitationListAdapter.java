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
import ru.startandroid.purchaselist.chat.model.Invitation;
import ru.startandroid.purchaselist.views.InvitationListViewInterface;

/**
 * Created by user on 26/03/2018.
 */

public class InvitationListAdapter extends RecyclerView.Adapter<InvitationListAdapter.ViewHolder>  {

    private String today;
    private String yesterday;
    private List<Invitation> invitationList;

    public InvitationListAdapter(InvitationListViewInterface invitationListView){
        invitationList = invitationListView.getInvitationList();
    }
    @Override
    public InvitationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_list_item, parent, false);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        today = sdf.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        yesterday = sdf.format(calendar.getTime());
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(InvitationListAdapter.ViewHolder holder, int position) {
        holder.senderName.setText("from " + invitationList.get(position).getSenderName());

        String[] dateArray = invitationList.get(position).getDate().split(" ");
        StringBuilder sb = new StringBuilder();
        if(dateArray[0].equals(today)) {
            sb.delete(0, sb.length());
            sb.append("Today").append(" at ").append(dateArray[1]);
            holder.sendDate.setText(sb.toString());
        }
        else if(dateArray[0].equals(yesterday)) {
            sb.delete(0, sb.length());
            sb.append("Yesterday").append(" at ").append(dateArray[1]);
            holder.sendDate.setText(sb.toString());
        }
        else
            holder.sendDate.setText(invitationList.get(position).getDate());

    }

    @Override
    public int getItemCount() {

        return invitationList == null ? 0 : invitationList.size();
    }

 class ViewHolder extends RecyclerView.ViewHolder{

     @BindView(R.id.inviteSenderNameTv)
     TextView senderName;

     @BindView(R.id.dateInInvitation)
     TextView sendDate;

     protected ViewHolder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);

         senderName = (TextView) itemView.findViewById(R.id.inviteSenderNameTv);
         sendDate = (TextView) itemView.findViewById(R.id.dateInInvitation);
     }
 }
}
