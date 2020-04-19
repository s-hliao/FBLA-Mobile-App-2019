 package com.hg.mad.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hg.mad.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

 /**
 * Dialog Fragment containing filter form.
 */
public class FilterChapDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface FilterListener {

        void onFilter(ChapFilters filters);

    }

    private View rootView;

    private EditText eventType;
    private EditText startDate;
    private EditText endDate;

    private LinearLayout todayStart;
    private LinearLayout todayEnd;

    private Button cancel;
    private Button apply;

    private FilterListener filterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_filter_chap, container, false);

        eventType = rootView.findViewById(R.id.type_text);
        startDate = rootView.findViewById(R.id.start_text);
        endDate = rootView.findViewById(R.id.end_text);

        todayStart = rootView.findViewById(R.id.layout_today_start);
        todayEnd = rootView.findViewById(R.id.layout_today_end);
        cancel = rootView.findViewById(R.id.button_cancel);
        apply = rootView.findViewById(R.id.button_search);

        todayStart.setOnClickListener(this);
        todayEnd.setOnClickListener(this);
        cancel.setOnClickListener(this);
        apply.setOnClickListener(this);

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
            case R.id.layout_today_start:
                onTodayStartClicked();
            case R.id.layout_today_end:
                onTodayEndClicked();
            case R.id.button_search:
                onSearchClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void onTodayStartClicked(){
        SimpleDateFormat dateFormat= new SimpleDateFormat("MM/dd/yyyy");
        startDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
     }

     public void onTodayEndClicked(){
         SimpleDateFormat dateFormat= new SimpleDateFormat("MM/dd/yyyy");
         endDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
     }

    public void onSearchClicked() {

        if (filterListener != null) {
            filterListener.onFilter(getFilters());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getType() {
        String type = eventType.getText().toString();
        return type;
    }

    @Nullable
    private Date getStartDate() {
        SimpleDateFormat dateFormat= new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date start = dateFormat.parse(startDate.getText().toString());
            return start;
        } catch(Exception e){
            if(startDate.getText().toString().equals(null)||startDate.getText().toString().equals("")){
                return null;
            } else{
                Toast.makeText(getContext(), "Incorrect Date Format", Toast.LENGTH_SHORT).show();
            }

        }
        return null;
    }

    private Date getEndDate() {
        SimpleDateFormat dateFormat= new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date start = dateFormat.parse(endDate.getText().toString());
            return start;
        } catch(Exception e){
            if(endDate.getText().toString().equals(null)||endDate.getText().toString().equals("")){
                return null;
            } else{
                Toast.makeText(getContext(), "Incorrect Date Format", Toast.LENGTH_SHORT).show();
            }

        }
        return null;

    }

    public ChapFilters getFilters() {
        ChapFilters filters = new ChapFilters();

        if (rootView != null) {
            filters.setType(getType());
            filters.setStartDate(getStartDate());
            filters.setEndDate(getEndDate());
        }

        return filters;
    }
}
