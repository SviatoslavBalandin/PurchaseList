package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.di.AccountMainListModule;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.presenters.AccountPresenter;
import ru.startandroid.purchaselist.presenters.AccountPresenterImpl;
import ru.startandroid.purchaselist.views.helpers.AlarmDialog;
import ru.startandroid.purchaselist.views.helpers.AlarmOnClickListener;
import ru.startandroid.purchaselist.views.helpers.ClickListener;
import ru.startandroid.purchaselist.views.helpers.MainListAdapter;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;
import ru.startandroid.purchaselist.views.helpers.RenameDeleteDialog;
import ru.startandroid.purchaselist.views.helpers.RenameDeleteDialogListener;

/**
 * Created by user on 16/10/2017.
 */

public class AccountFragment extends Fragment implements AccountScreenView, AlarmOnClickListener{

    @Inject
    AccountPresenter presenter;

    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;
    @BindView(R.id.fabDelete) FloatingActionButton fabDelete;
    @BindView(R.id.listOfLists) RecyclerView recyclerViewMainList;
    @BindView(R.id.main_ToolBar)
    Toolbar toolbar;

    private List<GoodsList> mainList;
    private MainViewInterface mainActivity;
    private MainListAdapter mainListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        ButterKnife.bind(this, view);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity = (MainViewInterface) getActivity();
        toolbar.setNavigationIcon(R.drawable.ic_acc_nav3);
        toolbar.setNavigationOnClickListener(v -> mainActivity.showDrawer());
        recyclerViewMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainList = new ArrayList<>();
        mainListAdapter = new MainListAdapter(this);
        presenter.fetchLists();
        attachOnClickListenerToRecyclerView(recyclerViewMainList);
        presenter.reactOnAnswer();
        return view;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createUserComponent(new AccountMainListModule(this)).inject(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.removeUnusedAnswers();
        presenter.stopListen();
    }

    @OnClick(R.id.fabAdd)
    public void onAddList() {
       presenter.addList();
   }

    @OnClick(R.id.fabDelete)
    public void onDeleteList(){
        AlarmDialog dialog = new AlarmDialog(this,0);
        dialog.setAlarmDialogTitle(R.string.delete_list_dialog);
        dialog.show(getFragmentManager(), "AlarmDialog");
    }
    @Override
    public void onPositiveClick(int dialogId) {
        presenter.deleteList();
    }

    @Override
    public SharedPreferences getMainPreferences() {
        return mainActivity.getPreferences();
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return mainActivity.getPrivatePreferences();
    }

    @Override
    public List<GoodsList> getMainList() {
        return mainList;
    }

    @Override
    public void showProducts() {
        recyclerViewMainList.setAdapter(mainListAdapter);
    }

    @Override
    public void refreshList() {
        mainListAdapter.notifyDataSetChanged();
    }

    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View v, int position) {
                mainActivity.onSwitch();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, new ShoppingListFragment(mainList.get(position)))
                        .commit();
            }
            @Override
            public void onLongClick(View v, int position) {
                if(!mainActivity.getDrawerStatus())
                    openRenameDeleteDialog(position);
            }
        }));
    }
    private void openRenameDeleteDialog(int position){
        new RenameDeleteDialog(new RenameDeleteDialogListener() {
            @Override
            public void onRename() {
                new RenameListDialogOutside(presenter, position, getMainList()).show(getChildFragmentManager(), "RenameListDialog");
            }

            @Override
            public void onDelete() {
                presenter.deleteList(position);
            }

        }).show(getFragmentManager(), "RenameDeleteFragment");
    }

}
