package com.hg.mad.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hg.mad.ui.LinksFragment;
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
                return new LinksFragment();
            case 1:
                return new MyChapFragment();
            case 2:
                return new MyCompFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Links";
            case 1:
                return "Chapter";
            case 2:
                return "Competitive";
            default:
                return null;
        }
    }
}
