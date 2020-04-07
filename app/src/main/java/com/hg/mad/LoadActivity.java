package com.hg.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticMethods.redirect(this);
    }
}
