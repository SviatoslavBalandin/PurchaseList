package ru.startandroid.purchaselist.chat.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.startandroid.purchaselist.chat.ChatRootFragment;
import ru.startandroid.purchaselist.chat.view.ChatRootFragmentInterface;
import ru.startandroid.purchaselist.chat.view.ChatView;
import ru.startandroid.purchaselist.chat.view.MyFriendsView;
import ru.startandroid.purchaselist.chat.view.PeopleView;

/**
 * Created by user on 16/03/2018.
 */

public class ChatFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ChatRootFragmentInterface chatRootFragment;
    public ChatFragmentPagerAdapter(FragmentManager fragmentManager, ChatRootFragmentInterface chatRootFragment) {
        super(fragmentManager);
        this.chatRootFragment = chatRootFragment;
    }

    @Override
    public Fragment getItem(int position) {
       switch (position){
           case 0:
               return new ChatView(chatRootFragment);
           case 1:
               return new PeopleView(chatRootFragment);
           default:
               break;
       }
       return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
