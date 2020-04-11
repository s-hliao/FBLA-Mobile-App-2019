package com.hg.mad.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hg.mad.ui.ByEventsFragment;
import com.hg.mad.ui.ByStudentsFragment;
import com.hg.mad.ui.LinksFragment;
import com.hg.mad.ui.MyChapFragment;
import com.hg.mad.ui.MyCompFragment;

public class ManageCompPagerAdapter extends FragmentPagerAdapter {

    public ManageCompPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ByStudentsFragment();
            case 1:
                return new ByEventsFragment();
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
                return "By Students";
            case 1:
                return "By Events";
            default:
                return null;
        }
    }
}
