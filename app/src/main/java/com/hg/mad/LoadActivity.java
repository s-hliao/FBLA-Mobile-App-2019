package com.hg.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hg.mad.util.ThisUser;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThisUser.redirect(this);
    }
}
