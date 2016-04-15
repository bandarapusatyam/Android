package com.example.satyam.imagedownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * Full screen view of the image like in WhatsApp
 */
public class FullscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fullscreen activity settings
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        setContentView(R.layout.activity_fullscreen);
        RotateAndSetFullImage(getIntent().getStringExtra("imagepath"));
    }

    //Rotate the image to 180 degrees and set in fullscreen view
    void RotateAndSetFullImage(String path){
        ImageView fullImg = (ImageView)findViewById(R.id.fullIcon);
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            DisplayMetrics dm= getApplicationContext().getResources().getDisplayMetrics();
            Bitmap srcBitmap =  ImageUtilities.decodeSampledBitmapFromFile(path, dm.widthPixels, dm.widthPixels);
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(),matrix,true);
            fullImg.setImageBitmap(srcBitmap);
        }catch (OutOfMemoryError ex){
            Toast.makeText(getApplicationContext(), "OOPs, Image is too huge. Out of memory " , Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
