package com.example.satyam.imagedownloader;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class DownloadIntentService extends IntentService {

    public DownloadIntentService() {
        super("DownloadIntentService");
    }


    protected void onHandleIntent(Intent intent) {
        DownloadUtilities.DownloadImage(intent);
    }

    public void onDestroy(){
        DownloadUtilities.isCancelled = true;
        super.onDestroy();
    }
}
