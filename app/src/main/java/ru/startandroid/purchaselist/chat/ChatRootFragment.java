package ru.startandroid.purchaselist.chat;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.helpers.ChatFragmentPagerAdapter;
import ru.startandroid.purchaselist.chat.view.ChatRootFragmentInterface;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.views.MainViewInterface;
import ru.startandroid.purchaselist.views.ShoppingListFragment;

/**
 * Created by user on 22/03/2018.
 */

public class ChatRootFragment extends Fragment implements ChatRootFragmentInterface {

    @BindView(R.id.chatFragmentTabLayout)
    TabLayout tabLayout;
    @BindView(R.id.chatFragmentViewPager)
    ViewPager viewPager;

    private FragmentManager fragmentManager;
    private android.app.FragmentManager mainFragmentManager;
    private MainViewInterface mainView;
    private GoodsList parentReference;

    public ChatRootFragment(){}

    @SuppressLint("ValidFragment")
    public ChatRootFragment(FragmentManager fragmentManager, GoodsList parentReference){
        this.fragmentManager = fragmentManager;
        this.parentReference = new GoodsList(parentReference.getTitle(), parentReference.getDate(),
                parentReference.getListId(), parentReference.getOwner(), parentReference.isOwner);
        this.parentReference.setConnectionId(parentReference.getConnectionId());
        this.parentReference.setGuests(parentReference.getGuests());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_chat_root_fragment, null);
        mainView = (MainViewInterface) getActivity();
        ButterKnife.bind(this, v);
        mainFragmentManager = getFragmentManager();
        viewPager.setAdapter(new ChatFragmentPagerAdapter(fragmentManager, this, checkIsOwner()));
        tabLayout.setupWithViewPager(viewPager);
    return v;
    }
    @Override
    public void backToList() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_container, new ShoppingListFragment(parentReference))
                .commit();
    }

    @Override
    public SharedPreferences getMainPreferences() {
        return mainView.getPreferences();
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return mainView.getPrivatePreferences();
    }

    @Override
    public android.app.FragmentManager getMainFragmentManager() {
        return mainFragmentManager;
    }

    @Override
    public GoodsList getParentList() {
        return parentReference;
    }


    private int checkIsOwner(){
        if(parentReference.isOwner)
            return 2;
        else
            return 1;
    }
}
