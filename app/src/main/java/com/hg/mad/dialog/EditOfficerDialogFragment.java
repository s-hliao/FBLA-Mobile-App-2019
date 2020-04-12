 package com.hg.mad.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hg.mad.R;

import java.util.List;

 /**
 * Dialog Fragment containing filter form.
 */
public class EditOfficerDialogFragment extends DialogFragment implements View.OnClickListener {


    private View rootView;

    private Spinner typeSpinner;
    private Spinner categorySpinner;
    private Spinner introSpinner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_filter, container, false);

        typeSpinner = rootView.findViewById(R.id.spinner_type);
        categorySpinner = rootView.findViewById(R.id.spinner_category);
        introSpinner = rootView.findViewById(R.id.spinner_intro);

        rootView.findViewById(R.id.button_search).setOnClickListener(this);
        rootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        FragmentManager fragmentManager = getFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if(fragment != null && fragment.isVisible()) {
                if (fragment instanceof FilterListener) {
                    filterListener = (FilterListener) fragment;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                onSearchClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void onSearchClicked() {


    }

    public void onCancelClicked() {
        dismiss();
    }


}
