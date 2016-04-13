package com.example.satyam.imagedownloader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by satyam on 3/19/16.
 * downloading the image asynchronously.
 */

public class DownloadImageTask extends AsyncTask<String, Integer, String> {

    // to set the downloaded image to imageview
    private WeakReference<MainActivity> mActivityRef;
    //for preventing the sleep mode.
    private PowerManager.WakeLock mWakeLock;

    public DownloadImageTask(MainActivity mActivity) {
        super();
        mActivityRef = new WeakReference<MainActivity>(mActivity);
    }


    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        MainActivity mActivity = mActivityRef.get();
        if(mActivity!=null) {
            PowerManager pm = (PowerManager) mActivity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, mActivity.getClass().getName());
            mWakeLock.acquire();
            mActivity.mProgressDialog.show();
        }
    }

    //downloading image in background thread.
    protected String doInBackground(String... urls) {
        return DownloadUtilities.DownloadAndSaveImage(urls[0], null, this);
    }

    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        MainActivity mActivity = mActivityRef.get();
        if(mActivity!=null) {
            // if we get here, length is known, now set indeterminate to false
            mActivity.mProgressDialog.setIndeterminate(false);
            mActivity.mProgressDialog.setMax(100);
            mActivity.mProgressDialog.setProgress(progress[0]);
        }
    }

    protected void onPostExecute(String result) {
        mWakeLock.release();
        MainActivity mActivity = mActivityRef.get();
        if(mActivity!=null) {
            mActivity.mProgressDialog.dismiss();
            // display image only if successfully downloaded(result == null)
            if (result != null) {
                mActivity.DisplayImage(result);
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "Download error: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void publishProgress(int progress){
        super.publishProgress(progress);
    }
}
