package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.chat.view.ChatViewInterface;

/**
 * Created by user on 22/03/2018.
 */

public class ChatListViewAdapter extends RecyclerView.Adapter<ChatListViewAdapter.ChatListViewHolder> {

    private List<Message> messageList;
    private String today;
    private String yesterday;
    private String ownerName;

    public ChatListViewAdapter(ChatViewInterface chatView){
        messageList = chatView.getMessageList();
        ownerName = chatView.getPrivatePreferences().getString("current user name", "");
    }

    @Override
    public ChatListViewAdapter.ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        today = sdf.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        yesterday = sdf.format(calendar.getTime());
        return new ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {

        checkIsOwner(messageList.get(position), holder);

        holder.messageContent.setText(messageList.get(position).getContent());
        holder.messageOwner.setText(messageList.get(position).getNameOfCreator());

        String[] dateArray = messageList.get(position).getDate().split(" ");
        StringBuilder sb = new StringBuilder();
        if(dateArray[0].equals(today)) {
            sb.delete(0, sb.length());
            sb.append("Today").append(" at ").append(dateArray[1]);
            holder.messageDate.setText(sb.toString());
        }
        else if(dateArray[0].equals(yesterday)) {
            sb.delete(0, sb.length());
            sb.append("Yesterday").append(" at ").append(dateArray[1]);
            holder.messageDate.setText(sb.toString());
        }
        else
            holder.messageDate.setText(messageList.get(position).getDate());

    }
    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    private void checkIsOwner(Message message ,ChatListViewHolder holder){
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
        RelativeLayout.LayoutParams params2 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );

        if(!message.getNameOfCreator().equals(ownerName)) {
           params.addRule(RelativeLayout.ALIGN_PARENT_END);
           params2.addRule(RelativeLayout.BELOW, R.id.messageCover);
           params2.addRule(RelativeLayout.ALIGN_PARENT_END);
           params2.topMargin = 5;
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params2.addRule(RelativeLayout.BELOW, R.id.messageCover);
            params2.addRule(RelativeLayout.ALIGN_PARENT_START);
            params2.topMargin = 5;
        }

        holder.messageCover.setLayoutParams(params);
        holder.userNickNameContainer.setLayoutParams(params2);
    }
    class ChatListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userNickNameContainer)
        LinearLayout userNickNameContainer;

        @BindView(R.id.messageCover)
        RelativeLayout messageCover;

        @BindView(R.id.chatMessageDate)
        TextView messageDate;

        @BindView(R.id.chatMessageContent)
        TextView messageContent;

        @BindView(R.id.currentUserNickNameTv)
        TextView messageOwner;

        ChatListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
