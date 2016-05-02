package com.example.satyam.imagedownloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    //cancelling image download
    ProgressDialog mProgressDialog;
    //image saved path for displaying in fullscreen mode.
    String imagePath;
    DownloadPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting buttons and imageview listener
        findViewById(R.id.button1).setOnTouchListener(this);
        findViewById(R.id.button2).setOnTouchListener(this);
        findViewById(R.id.button3).setOnTouchListener(this);
        findViewById(R.id.imageView).setOnTouchListener(this);
        setUpProgressDialog();
        presenter = new DownloadPresenter(this);
    }

    //progress dialog to show download status
    void setUpProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading Image...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
    }

    //Listener for button click and imageView tap
    public boolean onTouch(View view, MotionEvent event) {
        return presenter.OnTouch(view, event);
    }


    void prepareDownloadAsyncTask() {
        String url = ((EditText) findViewById(R.id.editText)).getText().toString();
        if (url != null && !url.isEmpty()) {
            DownloadUtilities.isCancelled = false;
            final DownloadImageTask downloadTask = presenter.startDownloadAsyncTask(url);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DownloadUtilities.isCancelled = true;
                    downloadTask.cancel(true);
                }
            });
        } else {
            displayToast("Please enter the URL ");
        }
    }

    void prepareDownloadService(final Intent intent) {
        String url = ((EditText) findViewById(R.id.editText)).getText().toString();
        if (url != null && !url.isEmpty()) {
            mProgressDialog.show();
            DownloadUtilities.isCancelled = false;
            presenter.startDownloadService(intent, url);
            mProgressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            stopService(intent);
                        }
                    });
        } else {
            displayToast("Please enter the URL ");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void displayToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    //Display the image in the screen(in the ImageView) after downloading.
    void DisplayImage(String path) {
        imagePath = path;
        presenter.DisplayImage(imagePath, (ImageView) findViewById(R.id.imageView));
    }
}

