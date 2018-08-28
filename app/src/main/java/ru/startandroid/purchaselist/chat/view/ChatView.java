package ru.startandroid.purchaselist.chat.view;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.helpers.ChatListViewAdapter;
import ru.startandroid.purchaselist.chat.model.Message;
import ru.startandroid.purchaselist.chat.presenters.ChatPresenter;
import ru.startandroid.purchaselist.di.ChatModule;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.views.MainViewInterface;
import ru.startandroid.purchaselist.views.helpers.AlarmDialog;
import ru.startandroid.purchaselist.views.helpers.AlarmOnClickListener;

/**
 * Created by user on 16/03/2018.
 */

public class ChatView extends Fragment implements ChatViewInterface, AlarmOnClickListener {

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
    @BindView(R.id.chatViewToolbarCleanChatHistoryBtn)
    ImageButton cleanHistoryBtn;
    @BindView(R.id.chatViewToolbarPermissionsBtn)
    ImageButton permissionsBtn;
    @BindView(R.id.chatViewToolbarTitle)
    TextView toolbarTitle;

    private ArrayList<Message> messageList;
    private ChatRootFragmentInterface chatRootFragment;
    private  ChatListViewAdapter adapter;
    private MainViewInterface mainView;

    public ChatView(){}

    @SuppressLint("ValidFragment")
    public ChatView(ChatRootFragmentInterface chatRootFragment){
        this.chatRootFragment = chatRootFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_view, null);
        ButterKnife.bind(this, v);
        String listTitle = getPrivatePreferences().getString("listTitle","");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mainView = (MainViewInterface) activity;
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.chat_navigation_icon);
        toolbarTitle.setText(listTitle + " (Chat)");
        toolbar.setNavigationOnClickListener(v1 -> chatRootFragment.backToList());
        checkIfOwner();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        checkWallpaperSettings(parentLayout);
        listOfMessages.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();
        adapter = new ChatListViewAdapter(this);
        resolveDependencies();
        listOfMessages.setAdapter(adapter);
        chatPresenter.fetchAllMessages();
        refreshList();
        return v;
    }
    private void resolveDependencies(){
        MyApp.getAppComponent().createChatComponent(new ChatModule(this)).inject(this);
    }

    @Override
    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
        listOfMessages.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public SharedPreferences getPreferences() {
        return chatRootFragment.getMainPreferences();
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return chatRootFragment.getPrivatePreferences();
    }

    @Override
    public GoodsList getParentList() {
        return chatRootFragment.getParentList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatPresenter.stopListen();
    }
    @OnClick(R.id.btnSendMessage)
    public void sendMessage(){
        if(switchSendButtonEnabling()){
            chatPresenter.addMessage(editMessageEt.getText().toString());
            editMessageEt.setText("");
        }
    }
    @OnClick(R.id.chatViewToolbarCleanChatHistoryBtn)
    public void cleanHistory(){
        if(messageList.size() != 0) {
            AlarmDialog dialog = new AlarmDialog(this, 0);
            dialog.setAlarmDialogTitle(R.string.delete_chat_history);
            dialog.setNegativeButton(R.string.no);
            dialog.setPositiveButton(R.string.yes);
            dialog.show(chatRootFragment.getMainFragmentManager(), "AlarmDialog");
        }
    }
    @Override
    public void onPositiveClick(int dialogId) {
        chatPresenter.deleteAllMessages();
    }

    @OnClick(R.id.chatViewToolbarPermissionsBtn)
    public void clickOnPermissions(){
        chatRootFragment.getMainFragmentManager().beginTransaction()
                .replace(R.id.frag_container,
                        new PermissionView(getParentList()))
                .commit();
    }

    private boolean switchSendButtonEnabling(){
        return !TextUtils.isEmpty(chatRootFragment.getParentList().getConnectionId()) && !TextUtils.isEmpty(editMessageEt.getText().toString());
    }
    private void checkWallpaperSettings(CoordinatorLayout parentLayout){
        StringBuilder resource = new StringBuilder();
        final String prefValue = getPreferences().getString("wallpapers_get", "2");
        switch (prefValue){
            case "1":
                resource.delete(0, resource.length());
                resource.append("@drawable/chat_background_coffee");
                break;
            case "2":
                resource.delete(0, resource.length());
                resource.append("@drawable/chat_background_dark");
                break;
            case "3":
                resource.delete(0, resource.length());
                resource.append("@drawable/chat_background_light");
                break;
            default:
                break;
        }

        int id = getContext().getResources().getIdentifier(resource.toString(), "id", getContext().getPackageName());
        parentLayout.setBackground(getContext().getResources().getDrawable(id, null));

    }
    private void checkIfOwner(){
        if(getParentList().isOwner){
            cleanHistoryBtn.setVisibility(View.VISIBLE);
            permissionsBtn.setVisibility(View.VISIBLE);
        }else {
            cleanHistoryBtn.setVisibility(View.INVISIBLE);
            permissionsBtn.setVisibility(View.INVISIBLE);
        }

    }
}
