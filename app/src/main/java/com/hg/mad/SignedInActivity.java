package com.hg.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hg.mad.model.DatabaseUser;
import com.hg.mad.util.ThisUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Document;

public class SignedInActivity extends AppCompatActivity {

    public ThisUser thisUser;
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;

    private View headerView;

    @NonNull
    public static Intent createIntent(@NonNull Context context, @Nullable IdpResponse response) {
        return new Intent().setClass(context, SignedInActivity.class)
                .putExtra(ExtraConstants.IDP_RESPONSE, response);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chapter, R.id.nav_chapter_events, R.id.nav_competitive_events, R.id.nav_abtfbla, R.id.nav_help)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get the current user and wait till it finishes
        String[] methods = {"updateUserName", "updateChapterName"};
        thisUser = new ThisUser(this);
    }

    public void execute() {
        updateUsername();
        updateChapterName();
    }

    private void updateUsername(){
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        String username = thisUser.getDisplayName();

        if (thisUser.isAdmin())
            username += " (Admin)";

        navUsername.setText(username);
    }

    private void updateChapterName(){
        TextView navChapter = headerView.findViewById(R.id.nav_chapter);
        navChapter.setText(thisUser.getChapterName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signed_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Sign out
        if (item.getItemId() == R.id.action_signout){
            AuthUI.getInstance()
                    .signOut(getApplicationContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        // Show sign in screen
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(AuthUiActivity.createIntent(getApplicationContext()));
                            }
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
