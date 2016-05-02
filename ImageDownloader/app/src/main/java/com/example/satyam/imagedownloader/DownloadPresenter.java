package com.example.satyam.imagedownloader;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by satyam on 5/1/16.
 */
public class DownloadPresenter {

    MainActivity mActivity;

    public DownloadPresenter(MainActivity mainActivity){
        mActivity = mainActivity;
    }

    //downloading image in async
    public void Button1Clicked() {
        checkFilePermissions(null, Utilities.DOWNLOAD_ASYNC_TASK);
    }

    //downloading image in android service
    public void Button2Clicked() {
        checkFilePermissions(new Intent(mActivity, DownloadService.class), Utilities.DOWNLOAD_SERVICE);
    }

    //downloading image in android intent service
    public void Button3Clicked() {
        checkFilePermissions(new Intent(mActivity, DownloadIntentService.class), Utilities.DOWNLOAD_INTENT_SERVICE);
    }

    void checkFilePermissions(Intent intent, int requestFrom) {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(mActivity, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestFrom);
        } else {
            if (intent == null) {
                mActivity.prepareDownloadAsyncTask();
            } else {
                mActivity.prepareDownloadService(intent);
            }
        }
    }

    DownloadImageTask startDownloadAsyncTask(String url) {
        DownloadImageTask downloadTask = new DownloadImageTask(mActivity);
        downloadTask.execute(url);
        return downloadTask;
    }

    void startDownloadService(final Intent intent, String url) {
        DownloadUtilities.isCancelled = false;
        intent.putExtra("url", url);
        intent.putExtra("receiver", new DownloadReceiver(new Handler(), mActivity));
        mActivity.startService(intent);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Utilities.DOWNLOAD_ASYNC_TASK:
                    mActivity.prepareDownloadAsyncTask();
                    break;
                case Utilities.DOWNLOAD_INTENT_SERVICE:
                    mActivity.prepareDownloadService(new Intent(mActivity, DownloadIntentService.class));
                    break;
                case Utilities.DOWNLOAD_SERVICE:
                    mActivity.prepareDownloadService(new Intent(mActivity, DownloadService.class));
                    break;
            }
        } else {
            mActivity.displayToast("The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission");
        }
    }


    //Display the image in the screen(in the ImageView) after downloading.
    void DisplayImage(String path, ImageView imageView) {
        try {
            // loading image efficiently
            Bitmap srcBitmap = ImageUtilities.decodeSampledBitmapFromFile(path, imageView.getWidth(), imageView.getHeight());
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            // rotating image to 180 degrees.
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(srcBitmap);
        } catch (Exception ex) {
            mActivity.displayToast("Image resizing error ");
        }
    }

    // For showing image in fullscreen view
    void ShowFullImage() {
        if (mActivity.imagePath != null) {
            Intent intent = new Intent(mActivity, FullscreenActivity.class);
            intent.putExtra("imagepath", mActivity.imagePath);
            mActivity.startActivity(intent);
        }
    }


    public boolean OnTouch(View view, MotionEvent event){
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
}
