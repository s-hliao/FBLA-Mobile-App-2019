package com.hg.mad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hg.mad.ui.ChapterFragment;
import com.hg.mad.ui.LinksFragment;
import com.hg.mad.ui.MembersFragment;
import com.hg.mad.ui.MyChapFragment;
import com.hg.mad.ui.MyCompFragment;

public class ChapterPagerAdapter extends FragmentPagerAdapter {
    public ChapterPagerAdapter(@NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ChapterFragment();
            case 1:
                return new MembersFragment();
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
                return "Chapter Info";
            case 1:
                return "Members";
            default:
                return null;
        }
    }
}
