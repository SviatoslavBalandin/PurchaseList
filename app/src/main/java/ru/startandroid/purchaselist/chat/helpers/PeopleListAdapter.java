package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        if(peopleView.checkPerson(position)) {
            holder.peopleListItemCheckBox.setChecked(true);
        }
        else {
            holder.peopleListItemCheckBox.setChecked(false);

        }
    }

    @Override
    public int getItemCount() {
        return peopleList == null ? 0 : peopleList.size();
    }

    public void setFilter(List<UserInformation> newPeopleList){
        peopleList.clear();
        peopleList.addAll(newPeopleList);
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.personNickNamePeopleListItem)
        TextView nickNameTv;
        @BindView(R.id.peopleListItemCheckBox)
        CheckBox peopleListItemCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
