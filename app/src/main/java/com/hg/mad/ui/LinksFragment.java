package com.hg.mad.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.hg.mad.R;

public class LinksFragment extends Fragment {

    ListView listView;
    String mTitle[] = {
            "FBLA Website",
            "Competitive Events",
            "Facebook",
            "Twitter",
            "Instagram"
    };
    int images[] = {
            R.drawable.fbla_icon,
            R.drawable.fbla_icon,
            R.drawable.facebook_logo,
            R.drawable.twitter_logo,
            R.drawable.instagram_logo
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_links, container, false);
        listView = view.findViewById(R.id.list_links);
        ((ViewGroup)listView.getParent()).removeView(listView);
        final Context c = getContext();

        LinksFragment.MyAdapter adapter = new LinksFragment.MyAdapter(c, mTitle, images);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = null;
                switch (position){
                    case 0:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fbla-pbl.org/"));
                        break;
                    case 1:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fbla-pbl.org/fbla/competitive-events/"));
                        break;
                    case 2:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/FutureBusinessLeaders"));
                        break;
                    case 3:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/FBLA_National"));
                        break;
                    case 4:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fbla_pbl/"));
                        break;
                    case 5:
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/groups?gid=106458"));
                        break;
                }

                if (browserIntent != null)
                    startActivity(browserIntent);
            }
        });

        return listView;
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        int rImgs[];

        MyAdapter(Context c, String title[], int imgs[]) {
            super(c, R.layout.item_links, title);
            this.context = c;
            this.rTitle = title;
            this.rImgs = imgs;
        }

        View row;
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.item_links, parent, false);
            ImageView images = row.findViewById(R.id.linksImage);
            TextView myTitle = row.findViewById(R.id.linksMain);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);

            return row;
        }
    }
}
