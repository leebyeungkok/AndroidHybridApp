package com.lbo.hybridwebviewclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    public WebView mWebView;
    private Context mContext;

    private boolean bCmdProcess = false;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mWebView = (WebView)findViewById(R.id.webview);
        mProgressBar = this.findViewById(R.id.progressBar);
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setNetworkAvailable(true);
        mWebView.setWebChromeClient(new ChromeClient(this));
        mWebView.setWebViewClient( new MyWebViewClient(this) );
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
    private class MyWebViewClient extends WebViewClient {
        private Context pCtx;

        public MyWebViewClient(Context ctx) {
            pCtx = ctx;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                WebResourceRequest request) {

            Uri uri = Uri.parse(view.getUrl());
            return super.shouldOverrideUrlLoading(view, request);
        }
        @Override
        public void onPageStarted(	WebView view,
                                      String url,
                                      Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);

            mProgressBar.setVisibility (View.VISIBLE);

        }
        @Override
        public void onPageFinished(WebView view,
                                   String url)
        {
            super.onPageFinished(view, url);
            try {
                Thread.sleep(3000);
            }catch(Exception ex){}
            mProgressBar.setVisibility (View.GONE);
        }
        @Override
        public void onReceivedHttpError( WebView view,
                                         WebResourceRequest request,
                                         WebResourceResponse error)
        {
            super.onReceivedHttpError(view, request, error);
            new AlertDialog.Builder(pCtx)
                    .setTitle("확인")
                    .setMessage("Native Error" + error.getData().toString())
                    .setCancelable(false)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton)
                                {
                                    finish();
                                }
                            })
                    .show();
        }
    }

}