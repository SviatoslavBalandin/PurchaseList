package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.chat.view.ChatViewInterface;

/**
 * Created by user on 22/03/2018.
 */

public class ChatListViewAdapter extends RecyclerView.Adapter<ChatListViewAdapter.ChatListViewHolder> {

    private ChatViewInterface chatView;
    private List<Message> messageList;

    public ChatListViewAdapter(ChatViewInterface chatView){
        this.chatView = chatView;
        messageList = chatView.getMessageList();
    }

    @Override
    public ChatListViewAdapter.ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

        return new ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {

    }
    @Override
    public int getItemCount() {

        if(messageList == null)
            return 0;

            return messageList.size();
    }
    protected class ChatListViewHolder extends RecyclerView.ViewHolder {
        private TextView messageDate;
        private TextView messageContent;
        private TextView messageOwner;

        public ChatListViewHolder(View itemView) {
            super(itemView);
            messageDate = (TextView) itemView.findViewById(R.id.chatMessageDate);
            messageContent = (TextView) itemView.findViewById(R.id.chatMessageContent);
            messageOwner = (TextView) itemView.findViewById(R.id.currentUserNickNameTv);
        }
    }
}
