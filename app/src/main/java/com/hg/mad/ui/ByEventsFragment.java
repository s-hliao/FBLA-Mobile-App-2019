package com.hg.mad.ui;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.hg.mad.dialog.CompUsersSUDialogFragment;

public class ByEventsFragment extends CompetitiveEventsFragment{

    @Override
    protected void setVisibility() {
        manageButton.setVisibility(View.VISIBLE);
        manageButton.setText("Reset All Events");
    };

    @Override
    void onManageClicked() {
        // RESET ALL EVENTS

    }

    @Override
    public void onCompetitiveSelected(DocumentSnapshot competitiveEvent) {

        hideKeyboard();
        searchView.clearFocus();

        final String eventName = competitiveEvent.get("eventName").toString();

        // Show the users who signed up for this event
        CompUsersSUDialogFragment compUsersSUDialog = new CompUsersSUDialogFragment();
        compUsersSUDialog.setEventName(eventName);
        compUsersSUDialog.show(getFragmentManager(), "CompUsersSUDialog");
    }
}
