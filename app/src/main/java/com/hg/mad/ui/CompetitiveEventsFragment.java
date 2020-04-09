package com.hg.mad.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hg.mad.dialog.CompetitiveDialogFragment;
import com.hg.mad.dialog.Filters;
import com.hg.mad.R;
import com.hg.mad.dialog.FilterDialogFragment;
import com.hg.mad.adapter.CompetitiveAdapter;
import com.hg.mad.model.DatabaseUser;


public class CompetitiveEventsFragment extends Fragment implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        CompetitiveAdapter.OnCompetitiveListener {

    private RecyclerView competitiveRecycler;
    private SearchView searchView;
    private ImageView filterButton;
    private TextView manageButton;

    private FirebaseFirestore firestore;
    private Query query;

    private FilterDialogFragment filterDialog;
    private CompetitiveAdapter adapter;
    private Filters filters;
    private String searchText;

    private CompetitiveDialogFragment competitiveDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_competitive_events, container, false);

        // Set up views
        competitiveRecycler = root.findViewById(R.id.recycler_competitive);
        searchView = root.findViewById(R.id.search_competitive);
        filterButton = root.findViewById(R.id.button_filter);
        manageButton = root.findViewById(R.id.manage_signups);
        filterButton.setOnClickListener(this);
        manageButton.setOnClickListener(this);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();

        // Only show manage to admins
        DocumentReference databaseUserRef = firestore.collection("DatabaseUser").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DatabaseUser databaseUserClass = task.getResult().toObject(DatabaseUser.class);

                    if (databaseUserClass.getIsAdmin()){
                        manageButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        initRecyclerView();

        // Filter Dialog
        filterDialog = new FilterDialogFragment();
        filters = Filters.getDefault();

        initSearch();

        // Competitive Dialog
        competitiveDialog = new CompetitiveDialogFragment();

        return root;
    }

    private void initFirestore() {
        firestore = FirebaseFirestore.getInstance();

        query = firestore.collection("CompetitiveEvents")
                .orderBy("eventName", Query.Direction.ASCENDING);
    }

    private void initRecyclerView() {

        adapter = new CompetitiveAdapter(query, this);

        competitiveRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        competitiveRecycler.setAdapter(adapter);
        competitiveRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                onFilter(filters);

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Apply filters
        onFilter(filters);

        // Start listening for Firestore updates
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = firestore.collection("CompetitiveEvent");

        // Name (contains filter)
        if (searchText != null){

            String nameUpper = searchText.toUpperCase();
            String nameLower = searchText.toLowerCase();

            query = firestore.collection("CompetitiveEvent")
                    .orderBy("eventName")
                    .startAt(nameUpper)
                    .endAt(nameLower+"\uf8ff");
        }

        // Type (equality filter)
        if (filters.hasType()) {
            query = query.whereEqualTo("type", filters.getType());
        }

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // Intro (equality filter)
        if (filters.hasIntro()) {
            query = query.whereEqualTo("intro", filters.getIntro());
        }

        // Update the query
        this.query = query;
        adapter.setQuery(query);

        // Save filters
        this.filters = filters;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_filter:
                onFilterClicked();
                break;
            case R.id.manage_signups:
                onManageCLicked();
                break;
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        if (!filterDialog.isAdded())
            filterDialog.show(getFragmentManager(), "FilterDialog");
    }

    public void onManageCLicked() {
        // TODO
    }

    // TODO
    @Override
    public void onCompetitiveSelected(DocumentSnapshot competitiveEvent) {
        String eventName = competitiveEvent.get("eventName").toString();
        competitiveDialog.setEventName(eventName);
        competitiveDialog.show(getFragmentManager(), "CompetitiveDialog");

    }

}
