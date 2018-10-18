package ru.startandroid.purchaselist.chat.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.view.PermissionViewInterface;
import ru.startandroid.purchaselist.model.UserInformation;

/**
 * Created by user on 10/04/2018.
 */

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.ViewHolder>  {

    private List<UserInformation> dialogUsers;

    public PermissionAdapter(PermissionViewInterface permissionView){
        dialogUsers = permissionView.getDialogGuestsList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.permissions_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(dialogUsers.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return dialogUsers == null ? 0 : dialogUsers.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userNamePermissionsView)
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
