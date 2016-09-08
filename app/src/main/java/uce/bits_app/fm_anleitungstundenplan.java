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

/**
 * Created by Ubbo Eicke on 07.09.2016.
 * Eine WebView die die Setup-Seite anzeigt.
 */
public class fm_anleitungstundenplan extends Fragment {
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.webviewfm, container, false);
        mWebView = (WebView) v.findViewById(R.id.WVFragment);
        // URl zuzweisen
        mWebView.loadUrl("http://medivhus.ddns.net/hs/tutorial.php");

        // JavaScript erlauben
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // den WebView zwingen sich wirklich in einem WebView
        // innerhalb des Fragments und nicht im Browser zu öffnen
        mWebView.setWebViewClient(new WebViewClient());

        return v;
    }

}