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
import com.hg.mad.adapter.MyEventsPagerAdapter;

public class HomeFragment extends Fragment {

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up view pager and tab layout
        ViewPager viewPager = root.findViewById(R.id.my_events_pager);
        PagerAdapter pagerAdapter = new MyEventsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = root.findViewById(R.id.my_events_tab);
        tabLayout.setupWithViewPager(viewPager);
    }
}
