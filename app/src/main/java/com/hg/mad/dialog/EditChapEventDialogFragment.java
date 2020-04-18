package com.hg.mad.dialog;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class EditChapEventDialogFragment extends DialogFragment {

    private DocumentReference chapterEventReference;

    public void setChapterEventReference(DocumentReference chapterEventReference) {
        this.chapterEventReference = chapterEventReference;
    }
}
