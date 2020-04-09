package com.hg.mad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hg.mad.R;

public class AbtFBLAFragment extends Fragment {

    VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_abtfbla, container, false);

        WebView webView = root.findViewById(R.id.abtFBLAWeb);
        webView.loadUrl("file:///android_asset/aboutFBLA.html");

        return root;
    }
}
