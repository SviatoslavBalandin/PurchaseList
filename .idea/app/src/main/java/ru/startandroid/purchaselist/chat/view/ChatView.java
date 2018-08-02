package ru.startandroid.purchaselist.chat.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.helpers.ChatListViewAdapter;
import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenter;
import ru.startandroid.purchaselist.di.ChatModule;

/**
 * Created by user on 16/03/2018.
 */

public class ChatView extends Fragment implements ChatViewInterface {

    @Inject
    ChatPresenter chatPresenter;

    @BindView(R.id.chatViewParentLayout)
    CoordinatorLayout parentLayout;
    @BindView(R.id.editMessageEt)
    EditText editMessageEt;
    @BindView(R.id.btnSendMessage)
    Button btnSendMessage;
    @BindView(R.id.listOfMessages)
    RecyclerView listOfMessages;
    @BindView(R.id.mainChatToolBar)
    Toolbar toolbar;

    private ArrayList<Message> messageList;
    private ChatListViewAdapter adapter;
    private String listTitle;
    private String listId;
    private ChatRootFragmentInterface chatRootFragment;

    public ChatView(ChatRootFragmentInterface chatRootFragment){
        this.chatRootFragment = chatRootFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveDependencies();
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createChatComponent(new ChatModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_view, null);
        ButterKnife.bind(this, v);
        listTitle = getPreferences().getString("listTitle","");
        listId = getPreferences().getString("listId", "");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.chat_navigation_icon);
        toolbar.setTitle(listTitle + " (Chat)");
        toolbar.setNavigationOnClickListener(v1 -> chatRootFragment.backToList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        listOfMessages.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();
        adapter = new ChatListViewAdapter(this);

        refreshList();
        return v;
    }

    @Override
    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public void showMessages() {
        listOfMessages.setAdapter(adapter);
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
