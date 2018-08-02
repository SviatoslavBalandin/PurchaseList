package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.model.Invitation;
import ru.startandroid.purchaselist.di.InvitationModule;
import ru.startandroid.purchaselist.presenters.InvitationListPresenter;
import ru.startandroid.purchaselist.views.helpers.AlarmDialog;
import ru.startandroid.purchaselist.views.helpers.AlarmOnClickListener;
import ru.startandroid.purchaselist.views.helpers.ClickListener;
import ru.startandroid.purchaselist.views.helpers.InvitationListAdapter;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;

/**
 * Created by user on 26/03/2018.
 */

public class InvitationListView extends Fragment implements InvitationListViewInterface, AlarmOnClickListener {

    @Inject
    InvitationListPresenter presenter;

    @BindView(R.id.invitationBarLayout)
    AppBarLayout invitationBarLayout;
    @BindView(R.id.invitationToolBar)
    Toolbar toolbar;
    @BindView(R.id.invitationRecyclerList)
    RecyclerView invitationRecyclerList;
    @BindView(R.id.invitationListDeleteAll)
    Button deleteAll;

    private List<Invitation> invitationList;
    private InvitationListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invitation_list_view, null);
        ButterKnife.bind(this, v);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        MainViewInterface mainView = (MainViewInterface) activity;
        toolbar.setNavigationIcon(R.drawable.ic_acc_nav3);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        invitationRecyclerList.setLayoutManager(new LinearLayoutManager(activity));
        toolbar.setNavigationOnClickListener(view -> mainView.showDrawer());
        toolbar.setTitle(R.string.invitations);
        invitationList = new ArrayList<>();
        adapter = new InvitationListAdapter(this);
        resolveDependencies();
        new ItemTouchHelper(initItemTouchHelperSimpleCallback()).attachToRecyclerView(invitationRecyclerList);
        attachOnClickListenerToRecyclerView(invitationRecyclerList);
        presenter.fetchInvitation();
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createInvitationComponent(new InvitationModule(this)).inject(this);
    }

    @Override
    public void showInvitation() {
        invitationRecyclerList.setAdapter(adapter);
    }

    @Override
    public void refreshInvitationList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<Invitation> getInvitationList() {
        return invitationList;
    }

    @OnClick(R.id.invitationListDeleteAll)
    public void deleteAll(){
        if(invitationList.size() == 0)
            return;
        new AlarmDialog(this, 1).show(getFragmentManager(), "Alarm Dialog");
    }
    @Override
    public void onPositiveClick(int dialogId) {
        presenter.deleteAllInvitations();
    }

    private ItemTouchHelper.SimpleCallback initItemTouchHelperSimpleCallback(){
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT){
                    presenter.sendAnswer(getInvitationList().get(position), false);
                    presenter.deleteInvitation(getInvitationList().get(position));
                }
            }
        };
    }
    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Invitation invitation = invitationList.get(position);

                AlarmDialog dialog = new AlarmDialog(dialogId -> {
                    presenter.sendAnswer(invitation, true);
                    presenter.deleteInvitation(invitation);
                    refreshInvitationList();
                }, 2);
                dialog.setAlarmDialogTitle(invitation.getSenderName() + " offer You to join to \"" + invitation.getListTitle() + "\"");
                dialog.setNegativeButton(R.string.cancel);
                dialog.setPositiveButton(R.string.accept);
                dialog.show(getFragmentManager(), "Alarm Dialog2");
            }
            @Override
            public void onLongClick(View v, int position) {
            }
        }));
    }

}
