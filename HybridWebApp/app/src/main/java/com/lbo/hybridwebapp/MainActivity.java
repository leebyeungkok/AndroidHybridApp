package com.lbo.hybridwebapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    public WebView mWebView;
    private Context mContext;
    private Handler mHandler = new Handler();
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
        mWebView.addJavascriptInterface(
                new NativeBridge(this), "NativeBridge");
        //String url = "https://www.naver.com";
        String url = "file:///android_asset/carousel/index.html";
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

    private class NativeBridge {
        Activity mActivity;

        public NativeBridge(Activity activity) {
            mActivity = activity;
        }
        @JavascriptInterface
        public void callPhone(final String strPhoneNumber) {
            Log.i("callPhone", "START" + strPhoneNumber);
            if (bCmdProcess) return;
            bCmdProcess = true;
            try {
                mHandler.post(new Runnable() {
                    public void run() {
                        Intent dial = new Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:" + strPhoneNumber));
                        startActivity(dial);
                    }
                });
            } catch (Exception ex) {
                Log.e("PHONE Error", ex.toString());
            } finally {
                bCmdProcess = false;
            }
            Log.i("PHONE", "END");
        }

        // SMS 보내기
        @JavascriptInterface
        public void callSms(final String strPhoneNumber,
                            final String strSMS) {
            Log.i("callSms", "START" + strPhoneNumber);
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( mActivity, new String[] { android.Manifest.permission.SEND_SMS}, 1);
            }
            if (bCmdProcess) return;
            bCmdProcess = true;
            try {
                mHandler.post(new Runnable() {
                    public void run() {
                        Log.i("SMS", "START" + strPhoneNumber);
                        final SmsManager sms =
                                SmsManager.getDefault();
                        sms.sendTextMessage(strPhoneNumber,
                                null,
                                strSMS,
                                null, null);
                        Toast.makeText(mContext, "SMS를 보냈습니다.", Toast.LENGTH_LONG);
                    }
                });
            } catch (Exception ex) {
                Log.e("SMS Error", ex.toString());
            } finally {
                bCmdProcess = false;
            }
            Log.i("SMS", "END");
        }

        //  위치정보
        @JavascriptInterface
        public void callLocationPos(final String strCallbackFunc) {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Ask for permision
                ActivityCompat.requestPermissions( mActivity, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Ask for permision
                ActivityCompat.requestPermissions( mActivity, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            Log.i("CALL NativeBridge callLocationPos", "START");
            try {
                if (bCmdProcess) return;

                bCmdProcess = true;
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            LocationManager locMgr =
                                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                            String bestProv = locMgr.getBestProvider(criteria, true);
                            int result1= ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                            int result2= ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);


                            if ( result1 != PackageManager.PERMISSION_GRANTED &&

                                    result2 != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }


                            Location loc = locMgr.getLastKnownLocation(bestProv);
                            String strJavascript = strCallbackFunc + "('" +
                                    loc.getLatitude() + "','" +
                                    loc.getLongitude() + "')";
                            Log.e("", strJavascript);
                            mWebView.loadUrl("javascript:" + strJavascript);
                        }
                        catch(Exception exLoc)
                        {
                            String strJavascript = "alert('위치확인오류')";
                            mWebView.loadUrl("javascript:" + strJavascript);
                        }
                    }
                });
            }
            catch(Exception ex)
            {
                bCmdProcess = false;
                Log.e("Error", ex.toString());
            }
            finally
            {
                bCmdProcess = false;
            }
        }

        // 카메라 호출
        @JavascriptInterface
        public void callCamera() {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( mActivity, new String[] { android.Manifest.permission.CAMERA}, 1);
            }
            try {
                if (bCmdProcess) return;
                bCmdProcess = true;
                mHandler.post(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(mContext,
                                UtilCamera.class);
                        startActivity(intent);
                    }
                });
            } catch (Exception ex) {
                bCmdProcess = false;
                Log.e("Error", ex.toString());
            } finally {
                bCmdProcess = false;
            }
        }
    }

}