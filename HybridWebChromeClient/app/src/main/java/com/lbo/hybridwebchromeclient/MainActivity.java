package com.lbo.hybridwebchromeclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
        mWebView.setNetworkAvailable(true);
        mWebView.setWebChromeClient(new ChromeClient(this));
        //String url = "https://www.naver.com";
        String url = "file:///android_asset/index.html";
        mWebView.loadUrl(url);
    }
    private final class ChromeClient extends WebChromeClient {
        public Context mCtx;

        public ChromeClient(Context cxt) {
            mCtx = cxt;
        }
        @Override
        public boolean onJsAlert(WebView view,
                                 String url,
                                 String message,
                                 final JsResult result) {
            new AlertDialog.Builder(mCtx)
                    .setTitle("확인").setMessage(message)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false).show();
            return true;
        }
        @Override
        public boolean onJsConfirm(	WebView view,
                                       String url,
                                       String message,
                                       final JsResult result) {
            new AlertDialog.Builder(mCtx)
                    .setTitle("확인").setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.cancel();
                                }
                            })
                    .setCancelable(false).show();
            return true;
        }
    }
}