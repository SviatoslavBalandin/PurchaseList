package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.view.PeopleViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 28/03/2018.
 */

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder>{

    private PeopleViewInterface peopleView;
    private List<UserInformation> peopleList;

    public PeopleListAdapter(PeopleViewInterface peopleView){
        this.peopleView = peopleView;
        peopleList = peopleView.getPeopleList();
    }

    @Override
    public PeopleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nickNameTv.setText(peopleList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(peopleList == null)
            return 0;

        return peopleList.size();
    }
    public void setFilter(List<UserInformation> newPeopleList){
        peopleList.clear();
        peopleList.addAll(newPeopleList);
        notifyDataSetChanged();
    }
    private CompoundButton.OnCheckedChangeListener initOnCheckedChangeListener(PeopleListAdapter.ViewHolder holder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        };
    }
    private CompoundButton.OnCheckedChangeListener initReverseOnCheckedChangeListener(PeopleListAdapter.ViewHolder holder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        };
    }
        protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView nickNameTv;
        CheckBox peopleListItemCheckBox;
        public ViewHolder(View itemView) {
            super(itemView);
            nickNameTv = (TextView) itemView.findViewById(R.id.personNickNamePeopleListItem);
            peopleListItemCheckBox = (CheckBox) itemView.findViewById(R.id.peopleListItemCheckBox);
        }
    }
}
