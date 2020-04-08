package com.hg.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hg.mad.util.StaticMethods;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticMethods.redirect(this);
    }
}
