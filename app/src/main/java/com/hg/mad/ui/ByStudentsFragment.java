package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.hg.mad.R;
import com.hg.mad.adapter.MemberAdapter;
import com.hg.mad.adapter.MyCompEventAdapter;
import com.hg.mad.dialog.CompEventsSUDialogFragment;
import com.hg.mad.dialog.CompResetDialogFragment;
import com.hg.mad.dialog.CompSUDialogFragment;
import com.hg.mad.util.ThisUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ByStudentsFragment extends Fragment
        implements MemberAdapter.OnMemberListener {

    private View root;
    private TextView resetButton;

    private Query query;
    private MemberAdapter adapter;
    private RecyclerView compMembers;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_comp_members, container, false);

        compMembers = root.findViewById(R.id.recycler_comp_members);
        resetButton = root.findViewById(R.id.comp_student_reset);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompResetDialogFragment compResetDialog = new CompResetDialogFragment();

                getFragmentManager().executePendingTransactions();
                if (!compResetDialog.isAdded())
                    compResetDialog.show(getFragmentManager(), "CompResetDialog");
            }
        });

        String chapterName = ThisUser.getChapterName();
        query = FirebaseFirestore.getInstance().collection("DatabaseUser")
                .whereEqualTo("chapterName", chapterName)
                .orderBy("name");

        final MemberAdapter.OnMemberListener memberListener = this;
        adapter = new MemberAdapter(query, memberListener);
        adapter.setQuery(query);

        compMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        compMembers.setAdapter(adapter);
        compMembers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return root;
    }

    @Override
    public void onMemberSelected(DocumentSnapshot chapOfficer) {
        CompEventsSUDialogFragment compEventsSUDialog = new CompEventsSUDialogFragment();
        compEventsSUDialog.setName((String) chapOfficer.get("name"));
        compEventsSUDialog.setId(chapOfficer.getId());

        getFragmentManager().executePendingTransactions();
        if (!compEventsSUDialog.isAdded())
            compEventsSUDialog.show(getFragmentManager(), "compEventsSUDialog");
    }
}
