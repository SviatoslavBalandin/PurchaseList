package ru.startandroid.purchaselist.chat.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.ChatRootFragment;
import ru.startandroid.purchaselist.chat.helpers.PermissionAdapter;
import ru.startandroid.purchaselist.chat.presenters.PermissionPresenter;
import ru.startandroid.purchaselist.di.PermissionsModule;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.helpers.AlarmDialog;
import ru.startandroid.purchaselist.views.helpers.AlarmOnClickListener;
import ru.startandroid.purchaselist.views.helpers.ClickListener;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;

/**
 * Created by user on 10/04/2018.
 */

public class PermissionView extends Fragment implements PermissionViewInterface, AlarmOnClickListener {

    @Inject
    PermissionPresenter presenter;

    @BindView(R.id.permissViewToolBar)
    Toolbar toolbar;
    @BindView(R.id.permissViewRecyclerGuestList)
    RecyclerView recyclerGuestsList;
    @BindView(R.id.permissViewDeleteGuests)
    Button deleteDialogGuests;

    private List<UserInformation> dialogGuestList;
    private List<UserInformation> uselessGuests;
    private GoodsList parentListReference;
    private AppCompatActivity activity;
    private PermissionAdapter adapter;

    public PermissionView(){}

    @SuppressLint("ValidFragment")
    public PermissionView(GoodsList parentListReference){
        this.parentListReference = parentListReference;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.permissions_view, null);
        ButterKnife.bind(this, v);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.chat_navigation_icon);
        toolbar.setTitle(R.string.permissions);
        toolbar.setNavigationOnClickListener(initNavigationListener());
        recyclerGuestsList.setLayoutManager(new LinearLayoutManager(activity));
        attachOnClickListenerToRecyclerView(recyclerGuestsList);
        dialogGuestList = new ArrayList<>();
        uselessGuests = new ArrayList<>(5);
        adapter = new PermissionAdapter(this);
        resolveDependencies();
        presenter.fetchDialogGuests();
        refreshGuestsList();
        return v;
    }

    private void resolveDependencies(){
        MyApp.getAppComponent().createPermissionsComponent(new PermissionsModule(this)).inject(this);
    }

    @Override
    public void showDialogGuests() {
        recyclerGuestsList.setAdapter(adapter);
    }

    @Override
    public void refreshGuestsList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<UserInformation> getDialogGuestsList() {
        return dialogGuestList;
    }

    @Override
    public List<UserInformation> getUselessGuests() {
        return uselessGuests;
    }

    @Override
    public String getConnectionId() {
        return parentListReference.getConnectionId();
    }

    @OnClick(R.id.permissViewDeleteGuests)
    public void deleteDialogGuests(){
        if(uselessGuests.size() != 0) {
            AlarmDialog dialog = new AlarmDialog(this, 0);
            dialog.setAlarmDialogTitle(R.string.delete_useless_dialog_guests);
            dialog.setPositiveButton(R.string.yes);
            dialog.setNegativeButton(R.string.no);
            dialog.show(getFragmentManager(), "AlarmDialog");
        }
    }

    @Override
    public void onPositiveClick(int dialogId) {
        presenter.deleteDialogGuests(uselessGuests);

    }
    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View v, int position) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.permissionsViewItemCheckBox);
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.permissionsViewItemContainer);
                checkBox.toggle();
                if(checkBox.isChecked()){
                    if(!presenter.checkPerson(position)){
                        uselessGuests.add(dialogGuestList.get(position));
                        coordinatorLayout.setActivated(true);
                        activateDeleteButton();
                    }
                }else {
                    if(presenter.checkPerson(position)) {
                        uselessGuests.remove(uselessGuests.indexOf(dialogGuestList.get(position)));
                        coordinatorLayout.setActivated(false);
                        activateDeleteButton();
                    }
                }
            }
            @Override
            public void onLongClick(View v, int position) {
            }
        }));
    }
    private View.OnClickListener initNavigationListener(){
        return v -> getFragmentManager().beginTransaction()
                .replace(R.id.frag_container,
                        new ChatRootFragment(activity.getSupportFragmentManager(), parentListReference))
                .commit();

    }
    @Override
    public void activateDeleteButton(){
        if(uselessGuests.size() > 0){
            deleteDialogGuests.setEnabled(true);
            deleteDialogGuests.setBackgroundColor(getResources().getColor(R.color.red));
            deleteDialogGuests.setText(R.string.delete);
        }else {
            deleteDialogGuests.setEnabled(false);
            deleteDialogGuests.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            deleteDialogGuests.setText("");
        }
    }
}
