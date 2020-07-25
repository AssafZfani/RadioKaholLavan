package zfani.assaf.radiokahollavan.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;

public class UpdatesFragment extends BaseFragment {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View root = inflater.inflate(R.layout.fragment_updates, container, false);
            final WebView wbWebView = root.findViewById(R.id.wbWebView);
            wbWebView.getSettings().setJavaScriptEnabled(true);
            wbWebView.setWebViewClient(new WebViewClient());
            wbWebView.loadUrl("https://www.facebook.com/radiokahollavan");
            return root;
        } catch (Exception e) {
            Toast.makeText(container.getContext(), "יש לעדכן את גרסת ה-WebView במכשיר", Toast.LENGTH_LONG).show();
            return super.onCreateView(inflater, container, savedInstanceState);
        }

    }
}
