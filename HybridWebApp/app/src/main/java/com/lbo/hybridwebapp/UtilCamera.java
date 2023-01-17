package com.lbo.hybridwebapp;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class UtilCamera extends Activity {
    public static final int CAMERA_VIEW = 2000;
    private Uri mFileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = new Intent();
        intent.setAction(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(intent, CAMERA_VIEW);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}