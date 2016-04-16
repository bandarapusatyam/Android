package bandarapu.satyam.imagepuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends Activity {

    private GameView mGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        setContentView(R.layout.activity_game);

        mGameView = new GameView(getApplicationContext());
        setContentView(mGameView);
        if(getIntent().getStringExtra("comingFrom").equals("play") && GameUtilities.GetGameImagePath()==null){
            mGameView.startEngine(this,getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.flower));
        }else{
            try {
                if(getIntent().getStringExtra("comingFrom").equals("camera")||getIntent().getStringExtra("comingFrom").equals("gallery")){
                    GameUtilities.SetGameImagePath(Uri.parse(getIntent().getStringExtra("gameImage")));
                }
                mGameView.startEngine(this, getApplicationContext(),
                        MediaStore.Images.Media.getBitmap(getContentResolver(), GameUtilities.GetGameImagePath()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void DisplayGameOver(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                CustomDialog cdd = new CustomDialog(GameActivity.this);
                cdd.show();
            }
        });
    }

    public void prepareGameView(){
        mGameView.stopGame();
        mGameView.prepareGameEngine();
        mGameView.startGame();
    }

    public void DisplayErrorMessage(){
        stopGameThread();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this,R.style.MyAlertDialogStyle);

                alertDialogBuilder.setMessage("This image can't be loaded, Please select another.");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        GameUtilities.SetGameImagePath(null);
                        GameActivity.this.finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }

        });
    }
    public void stopGameThread(){
        mGameView.stopGame();
    }
}
