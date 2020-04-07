package com.hg.mad.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hg.mad.ContactUsActivity;
import com.hg.mad.FaqActivity;
import com.hg.mad.R;
import com.hg.mad.TermsActivity;

public class HelpFragment extends Fragment {

    ListView listView;
    String mTitle[] = {
            "FAQ",
            "Contact Us",
            "Terms of Use and Licensing",
    };
    String mDescription[] = {
            "Common questions on how to use MyChapter",
            "Need additional help?",
            "",
    };
    int images[] = {
            R.drawable.ic_menu_questions,
            R.drawable.ic_menu_contact,
            R.drawable.ic_menu_document,
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, container, false);
        listView = view.findViewById(R.id.text_dashboard);
        ((ViewGroup)listView.getParent()).removeView(listView);
        final Context c = getContext();

        HelpFragment.MyAdapter adapter = new HelpFragment.MyAdapter(c, mTitle, mDescription, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), FaqActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(getActivity(), ContactUsActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), TermsActivity.class));
                }
            }
        });

        return listView;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];

        MyAdapter(Context c, String title[], String description[], int imgs[]) {
            super(c, R.layout.category_row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }

        View row;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.category_row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);

            return row;
        }
    }
}
