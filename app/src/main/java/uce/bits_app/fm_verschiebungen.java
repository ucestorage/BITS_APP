package uce.bits_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class fm_verschiebungen extends Fragment {
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.webviewfm, container, false);
        mWebView = (WebView) v.findViewById(R.id.WVFragment);
        mWebView.loadUrl("http://medivhus.ddns.net/hs/app.php");


        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView.setWebViewClient(new WebViewClient());

        return v;
    }

}