package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.presenters.AccountPresenterImpl;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;
import ru.startandroid.purchaselist.views.helpers.RecyclerViewAdapter;

/**
 * Created by user on 16/10/2017.
 */

public class AccountFragment extends Fragment implements AccountScreenView {

    @Inject
    AccountPresenterImpl presenter;
    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;
    @BindView(R.id.fabDelete) FloatingActionButton fabDelete;
    @BindView(R.id.listOfLists) RecyclerView recyclerViewMainList;
    @BindView(R.id.main_ToolBar)
    Toolbar toolbar;

    private RecyclerViewAdapter recyclerViewAdapter;
    private List<GoodsList> mainList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveDependencies();
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createUserComponent(new AccountMainListModule(this)).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        ButterKnife.bind(this, view);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MainViewInterface mainActivity = (MainViewInterface) getActivity();
        toolbar.setNavigationIcon(R.drawable.ic_acc_nav3);
        toolbar.setNavigationOnClickListener(v -> mainActivity.showDrawer());
        recyclerViewMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainList = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        presenter.fetchLists();
        attachOnClickListenerToRecyclerView(recyclerViewMainList);
        recyclerViewAdapter.notifyDataSetChanged();
        return view;
    }
    @OnClick(R.id.fabAdd)
    public void onAddList() {
       presenter.addList();
   }

    @OnClick(R.id.fabDelete)
    public void onDeleteList(){
       presenter.deleteList();
    }

    @Override
    public SharedPreferences getMainPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public List<GoodsList> getMainList() {
        return mainList;
    }

    @Override
    public void showProducts() {
        recyclerViewMainList.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void refreshList() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ((MainViewInterface)getActivity()).onSwitch();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, new ShoppingListFragment(mainList.get(position).getListId(), mainList.get(position).getTitle()))
                        .commit();
            }
            @Override
            public void onLongItemClick(View v, int position) {
            }
        }));
    }

}
