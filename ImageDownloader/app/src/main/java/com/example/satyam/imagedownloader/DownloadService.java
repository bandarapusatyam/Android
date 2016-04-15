package com.example.satyam.imagedownloader;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.widget.Toast;
import android.os.Process;

import java.lang.ref.WeakReference;


public class DownloadService extends Service {


    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
     final static class ServiceHandler extends Handler {
        WeakReference<DownloadService> serviceRef;
        public ServiceHandler(Looper looper, DownloadService service) {
            super(looper);
            serviceRef = new WeakReference<DownloadService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            DownloadUtilities.DownloadImage((Intent) msg.obj);
            DownloadService service = serviceRef.get();
            service.stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceHandler = new ServiceHandler(thread.getLooper(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }


    public void onDestroy(){
        DownloadUtilities.isCancelled = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
