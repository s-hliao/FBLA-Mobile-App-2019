package com.hg.mad.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hg.mad.R;

public class AbtFBLAFragment extends Fragment {

    VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_abtfbla, container, false);

        WebView webView = root.findViewById(R.id.webView);
        webView.loadUrl("https://www.fbla-pbl.org/about/");
        WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        return root;
    }
}
