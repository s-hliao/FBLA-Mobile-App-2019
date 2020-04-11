package com.hg.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.hg.mad.adapter.ManageCompPagerAdapter;
import com.hg.mad.adapter.MyEventsPagerAdapter;

public class ManageCompActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_comp);

        getSupportActionBar().setTitle("Manage Sign Ups");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        // Set up view pager and tab layout
        ViewPager viewPager = findViewById(R.id.manage_comp_pager);
        PagerAdapter pagerAdapter = new ManageCompPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.manage_comp_tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ManageCompActivity.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
