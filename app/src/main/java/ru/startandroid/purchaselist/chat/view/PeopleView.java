package ru.startandroid.purchaselist.chat.view;

import android.annotation.SuppressLint;
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
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.helpers.Counter;
import ru.startandroid.purchaselist.chat.helpers.PeopleListAdapter;
import ru.startandroid.purchaselist.chat.presenters.PersonListPresenter;
import ru.startandroid.purchaselist.di.PeopleListViewModule;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.helpers.AlarmDialog;
import ru.startandroid.purchaselist.views.helpers.AlarmOnClickListener;
import ru.startandroid.purchaselist.views.helpers.ClickListener;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;

/**
 * Created by user on 16/03/2018.
 */

public class PeopleView extends Fragment implements PeopleViewInterface, AlarmOnClickListener {

    @Inject
    PersonListPresenter presenter;

    @BindView(R.id.peopleToolBar)
    Toolbar toolbar;
    @BindView(R.id.peopleRecyclerList)
    RecyclerView peopleRecyclerList;
    @BindView(R.id.tbPeopleNumberLimit)
    TextView peopleNumberLimitTv;
    @BindView(R.id.fabInvitePersons)
    FloatingActionButton fabInvitePersons;
    @BindView(R.id.peopleListSearchView)
    SearchView searchView;

    private List<UserInformation> staticPeopleList;
    private List<UserInformation> peopleList;
    private List<UserInformation> invitePersonsList;
    private ChatRootFragmentInterface chatRootFragment;
    private Counter counter;
    private PeopleListAdapter adapter;

    private static final int NUMBER_OF_INVITATIONS = 5;

    public PeopleView(){}

    @SuppressLint("ValidFragment")
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
        View view = inflater.inflate(R.layout.people_view, null);
        ButterKnife.bind(this, view);
        counter = new Counter(NUMBER_OF_INVITATIONS);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView.setOnQueryTextListener(initOnSearchListener());
        toolbar.setNavigationIcon(R.drawable.chat_navigation_icon);
        toolbar.setNavigationOnClickListener(v1 -> chatRootFragment.backToList());
        peopleRecyclerList.setLayoutManager(new LinearLayoutManager(activity));
        attachOnClickListenerToRecyclerView(peopleRecyclerList);
        staticPeopleList = new ArrayList<>();
        peopleList = new ArrayList<>();
        invitePersonsList = new ArrayList<>();
        adapter = new PeopleListAdapter(this);
        resolveDependencies();
        presenter.fetchPersons(true);
        peopleList.addAll(staticPeopleList);
        showPeopleList();
        return view;
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
    public  List<UserInformation> getInvitedPersonsList() {
        return invitePersonsList;
    }

    @Override
    public List<UserInformation> getStaticPeopleList() {
        return staticPeopleList;
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public SharedPreferences getPreferences() {
        return chatRootFragment.getPrivatePreferences();
    }

    @Override
    public boolean checkPerson(int position) {

        return presenter.checkPerson(position);
    }
    @OnClick(R.id.fabInvitePersons)
    public void  invitePersons(){
        if(invitePersonsList.size() == 0)
            return;
        AlarmDialog dialog = new AlarmDialog(this, 1);
        dialog.setAlarmDialogTitle(R.string.invitation_dialog_title);
        dialog.setNegativeButton(R.string.no);
        dialog.setPositiveButton(R.string.yes);
        dialog.show(chatRootFragment.getMainFragmentManager(), "Alarm Dialog");

    }
    @Override
    public void onPositiveClick(int dialogId) {

        presenter.invitePersons(invitePersonsList);
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
                    peopleList.clear();
                    peopleList.addAll(staticPeopleList);
                    List<UserInformation> filteredList = presenter.filter(newText);
                    adapter.setFilter(filteredList);
                return true;
            }
        };
    }
    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View v, int position) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.peopleListItemCheckBox);
                checkBox.toggle();
                if(checkBox.isChecked()){
                    if(!presenter.checkPerson(position)){
                        if(counter.add()) {
                            invitePersonsList.add(peopleList.get(position));
                            peopleNumberLimitTv.setText("(" + invitePersonsList.size() + "/5)");
                        }
                        else
                            checkBox.setChecked(false);
                    }
                }else {
                    if(presenter.checkPerson(position)) {
                        if (counter.deduct()) {
                            invitePersonsList.remove(invitePersonsList.indexOf(peopleList.get(position)));
                            peopleNumberLimitTv.setText("(" + invitePersonsList.size() + "/5)");
                        }
                    }
                }
            }
            @Override
            public void onLongClick(View v, int position) {
            }
        }));
    }
}