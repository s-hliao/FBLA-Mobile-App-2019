package com.hg.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
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
import androidx.viewpager.widget.PagerAdapter;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        thisUser = new ThisUser(this);
    }

    public void execute() {
        updateUsername();
        updateChapterName();
        setupKick();
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
        int itemID = item.getItemId();

        switch (itemID) {
            case (R.id.action_signout):
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
                break;

            case (R.id.action_leave):

                if (!ThisUser.isAdmin()) {

                    // Update Chapter
                    CollectionReference chaptersCollection = FirebaseFirestore.getInstance().collection("Chapter");
                    chaptersCollection.whereEqualTo("chapterName", ThisUser.getChapterName())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                DocumentSnapshot chapter = task.getResult().getDocuments().get(0);

                                Map<String, Map<String, String>> currentEventsChap = (Map<String, Map<String, String>>) chapter.get("competitiveEvents");

                                for (Map<String, String> event : currentEventsChap.values()){
                                    if (event.containsKey(ThisUser.getUid())){
                                        event.remove(ThisUser.getUid());
                                    }
                                }

                                ArrayList<String> keys = new ArrayList<>();
                                for(String key : currentEventsChap.keySet()){
                                    if (currentEventsChap.get(key).isEmpty()){
                                        keys.add(key);
                                    }
                                }
                                for (String key : keys){
                                    currentEventsChap.remove(key);
                                }

                                chapter.getReference().update("competitiveEvents", currentEventsChap);

                                DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(ThisUser.getUid());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("inChapter", false);
                                updates.put("isAdmin", false);
                                updates.put("chapterName", "");
                                updates.put("competitiveEvents", new HashMap<String, Integer>());
                                updates.put("chapterEvents", new HashMap<String, Integer>());

                                userRef.update(updates);

                                kicked();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "The admin cannot leave", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupKick() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("DatabaseUser").document(ThisUser.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (snapshot != null) {
                    if (! (Boolean) snapshot.get("inChapter")){
                        kicked();
                    }
                }
            }
        });
    }

    private void kicked() {
        FirebaseAuth.getInstance().signOut();
        startActivity(AuthUiActivity.createIntent(getApplicationContext()));
        Toast.makeText(getApplicationContext(), "You have been kicked", Toast.LENGTH_LONG).show();
    }

}
