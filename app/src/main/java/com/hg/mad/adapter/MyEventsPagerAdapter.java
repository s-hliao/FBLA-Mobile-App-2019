package com.hg.mad.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hg.mad.ui.MyChapFragment;
import com.hg.mad.ui.MyCompFragment;

public class MyEventsPagerAdapter extends FragmentPagerAdapter {

    public MyEventsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new MyChapFragment();
            case 1:
                return new MyCompFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Chapter Events";
            case 1:
                return "Competitive Events";
            default:
                return null;
        }
    }
}
