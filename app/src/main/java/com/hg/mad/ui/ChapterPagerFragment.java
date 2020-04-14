package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hg.mad.R;
import com.hg.mad.adapter.ChapterPagerAdapter;
import com.hg.mad.adapter.MyEventsPagerAdapter;

public class ChapterPagerFragment extends Fragment {

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_chapter_pages, container, false);

        // Set up view pager and tab layout
        ViewPager viewPager = root.findViewById(R.id.chapter_pager);
        PagerAdapter pagerAdapter = new ChapterPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = root.findViewById(R.id.chapter_tab);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }
}
