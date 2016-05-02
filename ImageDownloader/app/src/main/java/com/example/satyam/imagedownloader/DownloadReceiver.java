package com.example.satyam.imagedownloader;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by satyam on 5/1/16.
 */
public class DownloadReceiver  extends ResultReceiver {
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
