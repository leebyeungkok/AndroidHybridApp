package com.lbo.hybridbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
    public WebView mWebView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mWebView = (WebView)findViewById(R.id.webview);
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //String url = "https://www.naver.com";
        String url = "file:///android_asset/index.html";
        mWebView.loadUrl(url);
    }
}