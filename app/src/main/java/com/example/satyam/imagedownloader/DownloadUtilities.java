package com.example.satyam.imagedownloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by satyam on 4/9/16.
 */
public class DownloadUtilities {

    public static boolean isCancelled = false;

     static void DownloadImage(Intent intent){
        WeakReference<ResultReceiver> receiverRef = new WeakReference<ResultReceiver>((ResultReceiver) intent.getParcelableExtra("receiver"));
        //ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        String path = DownloadUtilities.DownloadAndSaveImage(intent.getStringExtra("url"), receiverRef.get(), null);
        if(receiverRef.get() != null) {
            Bundle resultData = new Bundle();
            resultData.putInt("progress", 100);
            resultData.putString("path", path);
            resultData.putString("end","end");
            receiverRef.get().send(Utilities.UPDATE_PROGRESS, resultData);
        }
    }

    //actual download and net connection here.
     static String DownloadAndSaveImage(String path, ResultReceiver resultReceiver, DownloadImageTask task) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(path).openConnection();
            connection.connect();

            // return error id if connection not succeed
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return null;
            }
            // to display download percentage
            // might be -1 if server did not report the length
            int fileLength = connection.getContentLength();
            // download the file. imageName is 'current time in milliseconds'.
            File imageFile = new File(Environment.getExternalStorageDirectory().toString(), System.currentTimeMillis() + ".jpg");
            output = new FileOutputStream(imageFile);
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            input = connection.getInputStream();
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (DownloadUtilities.isCancelled) {
                    input.close();
                    return null;
                }
                total += count;

                // publishing the progress....
                if (fileLength > 0){
                    int progress = (int) (total * 100 / fileLength);
                    if(resultReceiver != null){
                        Bundle resultData = new Bundle();
                        resultData.putInt("progress" , progress);
                        resultReceiver.send(Utilities.UPDATE_PROGRESS, resultData);
                    }else if(task != null) {
                        task.publishProgress(progress);
                    }
                }
                output.write(data, 0, count);
            }
            output.flush();
           return imageFile.getPath();
        } catch (Exception e) {
            System.out.println("Download error");
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}