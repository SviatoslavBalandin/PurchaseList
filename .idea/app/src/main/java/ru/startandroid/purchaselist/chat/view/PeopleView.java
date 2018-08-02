package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.helpers.Counter;
import ru.startandroid.purchaselist.chat.helpers.PeopleListAdapter;
import ru.startandroid.purchaselist.chat.presenters.PersonListPresenter;
import ru.startandroid.purchaselist.di.PeopleListViewModule;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;

/**
 * Created by user on 16/03/2018.
 */

public class PeopleView extends Fragment implements PeopleViewInterface {

    @Inject
    PersonListPresenter presenter;
    @Inject
    PeopleListAdapter adapter;

    @BindView(R.id.peopleToolBar)
    Toolbar toolbar;
    @BindView(R.id.peopleRecyclerList)
    RecyclerView peopleRecyclerList;
    @BindView(R.id.tbPeopleNumberLimit)
    TextView peopleNumberLimitTv;
    @BindView(R.id.fabAddPerson)
    FloatingActionButton fabAddPerson;
    @BindView(R.id.peopleListSearchView)
    SearchView searchView;

    private List<UserInformation> peopleList;
    private List<UserInformation> invitePersonsList;
    private String listTitle;
    private String listId;
    private ChatRootFragmentInterface chatRootFragment;
    private Counter counter;

    private static final int numberOfInvitations = 5;

    public PeopleView(ChatRootFragmentInterface chatRootFragment){
        this.chatRootFragment = chatRootFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.people_view, null);
        ButterKnife.bind(this, v);
        counter = new Counter(numberOfInvitations);
        listTitle = getPreferences().getString("listTitle","");
        listId = getPreferences().getString("listId", "");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView.setOnQueryTextListener(initOnSearchListener());
        toolbar.setNavigationIcon(R.drawable.chat_navigation_icon);
        toolbar.setNavigationOnClickListener(v1 -> chatRootFragment.backToList());
        peopleRecyclerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachOnClickListenerToRecyclerView(peopleRecyclerList);
        peopleList = new ArrayList<>();
        invitePersonsList = new ArrayList<>();
        resolveDependencies();
        presenter.fetchPersons(true);
        showPeopleList();
        refreshList();
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createPeopleListViewComponent(new PeopleListViewModule(this)).inject(this);
    }
    @Override
    public void showPeopleList() {
        peopleRecyclerList.setAdapter(adapter);
    }

    @Override
    public List<UserInformation> getPeopleList() {
        return peopleList;
    }

    @Override
    public List<UserInformation> getInvitedPersonsList() {
        return invitePersonsList;
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public SharedPreferences getPreferences() {
        return chatRootFragment.getPrivatePreferences();
    }

    private SearchView.OnQueryTextListener initOnSearchListener(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!searchView.isIconified()){
                    searchView.setIconified(true);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.fetchPersons(false);
                List<UserInformation> filteredList = presenter.filter(newText);
                adapter.setFilter(filteredList);
                return true;
            }
        };
    }
    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.peopleListItemCheckBox);
                checkBox.toggle();
                if(checkBox.isChecked()){
                    if(presenter.checkPerson(peopleList.get(position))){
                        if(counter.add())
                            invitePersonsList.add(peopleList.get(position));
                        else
                            checkBox.setChecked(false);
                    }
                }else {
                    if(!presenter.checkPerson(peopleList.get(position))) {
                        if (counter.deduct())
                            invitePersonsList.remove(peopleList.get(position));
                    }
                }
            }
            @Override
            public void onLongItemClick(View v, int position) {
            }
        }));
    }
}
