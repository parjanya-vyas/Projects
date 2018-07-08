package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView browser = (WebView)findViewById(R.id.web_site_area);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.setWebViewClient(new MyBrowser());
        browser.loadUrl("https://www.pottermore.com/");
    }

    protected class MyBrowser extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
