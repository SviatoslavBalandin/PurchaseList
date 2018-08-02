package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.startandroid.purchaselist.chat.view.PeopleViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 28/03/2018.
 */

public class ChatPersonsListAdapter extends RecyclerView.Adapter<ChatPersonsListAdapter.ViewHolder> {

    private PeopleViewInterface peopleView;
    private List<UserInformation> peopleList;
    public ChatPersonsListAdapter(PeopleViewInterface peopleView){
        this.peopleView = peopleView;
        peopleList = peopleView.getPeopleList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
