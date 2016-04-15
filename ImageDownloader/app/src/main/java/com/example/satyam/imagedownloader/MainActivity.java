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

public class MainActivity extends AppCompatActivity {

    //cancelling image download
    ProgressDialog mProgressDialog;
    //image saved path for displaying in fullscreen mode.
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting buttons and imageview listener
        findViewById(R.id.button1).setOnTouchListener(listener);
        findViewById(R.id.button2).setOnTouchListener(listener);
        findViewById(R.id.button3).setOnTouchListener(listener);
        findViewById(R.id.imageView).setOnTouchListener(listener);
        setUpProgressDialog();
    }

    //progress dialog to show download status
    void setUpProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading Image...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
    }

    //downloading image in async
    public void Button1Clicked() {
        checkFilePermissions(null, Utilities.DOWNLOAD_ASYNC_TASK);
    }

    //downloading image in android service
    public void Button2Clicked() {
        checkFilePermissions(new Intent(this, DownloadService.class), Utilities.DOWNLOAD_SERVICE);
    }

    //downloading image in android intent service
    public void Button3Clicked() {
        checkFilePermissions(new Intent(this, DownloadIntentService.class), Utilities.DOWNLOAD_INTENT_SERVICE);
    }

    void checkFilePermissions(Intent intent, int requestFrom) {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestFrom);
        } else {
            if (intent == null) {
                prepareAndStartAsyncTask();
            } else {
                prepareAndStartService(intent);
            }
        }
    }

    void prepareAndStartAsyncTask() {
        String url = ((EditText) findViewById(R.id.editText)).getText().toString();
        if (url != null && !url.isEmpty()) {
            DownloadUtilities.isCancelled = false;
            final DownloadImageTask downloadTask = new DownloadImageTask(this);
            downloadTask.execute(url);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DownloadUtilities.isCancelled = true;
                    downloadTask.cancel(true);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the URL ", Toast.LENGTH_LONG).show();
        }
    }

    void prepareAndStartService(final Intent intent) {
        String url = ((EditText) findViewById(R.id.editText)).getText().toString();
        if (url != null && !url.isEmpty()) {
            mProgressDialog.show();
            DownloadUtilities.isCancelled = false;
            intent.putExtra("url", url);
            intent.putExtra("receiver", new DownloadReceiver(new Handler(), this));
            startService(intent);
            mProgressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            stopService(intent);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the URL ", Toast.LENGTH_LONG).show();
        }
    }

    //Display the image in the screen(in the ImageView) after downloading.
    void DisplayImage(String path) {
        imagePath = path;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        try {
            // loading image efficiently
            Bitmap srcBitmap = ImageUtilities.decodeSampledBitmapFromFile(path, imageView.getWidth(), imageView.getHeight());
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            // rotating image to 180 degrees.
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(srcBitmap);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Image resizing error ", Toast.LENGTH_LONG).show();
        }
    }

    // For showing image in fullscreen view
    void ShowFullImage() {
        if (imagePath != null) {
            Intent intent = new Intent(this, FullscreenActivity.class);
            intent.putExtra("imagepath", imagePath);
            startActivity(intent);
        }
    }

    //Listener for button click and imageView tap
    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            boolean consumed = false;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                switch (view.getId()) {
                    case R.id.button1:
                        Button1Clicked();
                        consumed = true;
                        break;
                    case R.id.button2:
                        Button2Clicked();
                        consumed = true;
                        break;
                    case R.id.button3:
                        Button3Clicked();
                        consumed = true;
                        break;
                    case R.id.imageView:
                        ShowFullImage();
                        consumed = true;
                        break;
                    default:
                        break;
                }
            }
            return consumed;
        }
    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Utilities.DOWNLOAD_ASYNC_TASK:
                    prepareAndStartAsyncTask();
                    break;
                case Utilities.DOWNLOAD_INTENT_SERVICE:
                    prepareAndStartService(new Intent(this, DownloadIntentService.class));
                    break;
                case Utilities.DOWNLOAD_SERVICE:
                    prepareAndStartService(new Intent(this, DownloadService.class));
                    break;
            }
        } else {
            Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("ParcelCreator")
    static class DownloadReceiver extends ResultReceiver {
        WeakReference<MainActivity> activityRef;

        public DownloadReceiver(Handler handler, MainActivity activity) {
            super(handler);
            activityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Utilities.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                MainActivity mainActivity = activityRef.get();
                if (progress == 1) {
                    mainActivity.mProgressDialog.setIndeterminate(false);
                    mainActivity.mProgressDialog.setMax(100);
                }
                mainActivity.mProgressDialog.setProgress(progress);
                if (resultData.getString("end") != null) {
                    mainActivity.mProgressDialog.dismiss();
                    String path = resultData.getString("path");
                    if (path != null) {
                        mainActivity.DisplayImage(path);
                    } else {
                        Toast.makeText(mainActivity.getApplicationContext(), "OOPs, Download error ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

