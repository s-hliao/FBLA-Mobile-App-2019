package com.hg.mad.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.mutation.ArrayTransformOperation;
import com.hg.mad.R;
import com.hg.mad.adapter.MemberAdapter;
import com.hg.mad.adapter.OfficerAdapter;
import com.hg.mad.dialog.AddOfficerDialogFragment;
import com.hg.mad.dialog.EditOfficerDialogFragment;
import com.hg.mad.dialog.RemoveAllMembersDialog;
import com.hg.mad.dialog.RemoveMemberDialog;
import com.hg.mad.dialog.SocMediaDialogFragment;
import com.hg.mad.util.ThisUser;

import java.util.Map;

public class MembersFragment extends Fragment implements
        View.OnClickListener, MemberAdapter.OnMemberListener {
    private RecyclerView membersRV;
    private LinearLayout removeAll;

    private FirebaseFirestore firestore;
    private Query query;

    private MemberAdapter adapter;

    private boolean isAdmin;
    private String chapterName;

    private RemoveAllMembersDialog removeAllDialog;
    private RemoveMemberDialog removeDialog;

    @SuppressLint("WrongViewCast")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_members, container, false);

        membersRV = (RecyclerView) root.findViewById(R.id.recycler_members);
        removeAll = root.findViewById(R.id.btn_removeAll);

        removeAll.setOnClickListener(this);

        removeAllDialog = new RemoveAllMembersDialog();
        removeDialog  = new RemoveMemberDialog();

        FirebaseFirestore.setLoggingEnabled(true);

        firestore = FirebaseFirestore.getInstance();


        // Only show manage to admins

        isAdmin = ThisUser.isAdmin();
        if (isAdmin){
            removeAll.setVisibility(View.VISIBLE);
        } else{
            removeAll.setVisibility(View.INVISIBLE);
        }

        chapterName = ThisUser.getChapterName();

        final MemberAdapter.OnMemberListener memberListener = this;

        query = firestore.collection("DatabaseUser")
                .whereEqualTo("chapterName", chapterName)
                .orderBy("name");
        adapter = new MemberAdapter(query, memberListener);
        adapter.setQuery(query);
        membersRV.setLayoutManager(new LinearLayoutManager(getContext()));
        membersRV.setAdapter(adapter);
        membersRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
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
    }


    @Override
    public void onClick(View view) {
        switch( view.getId()){

            case R.id.btn_removeAll:
                getFragmentManager().executePendingTransactions();
                if(!removeAllDialog.isAdded())
                    removeAllDialog.show(getFragmentManager(), "removeAllDialog");
                break;
        }

    }


    @Override
    public void onMemberSelected(DocumentSnapshot member) {
        if(isAdmin){
            removeDialog.setMember(member);
            getFragmentManager().executePendingTransactions();
            if(!removeDialog.isAdded())
                removeDialog.show(getFragmentManager(), "removeDialog");

        }
    }
}
