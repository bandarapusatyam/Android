package bandarapu.satyam.imagepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MaincreenActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_CAPTURE = 2;
    private Uri cameraSavedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_maincreen);

        View view=findViewById(R.id.fullscreen_content);
        if(view!=null){
            view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.puzzle_animation));
        }
        findViewById(R.id.likeButton).setOnTouchListener(listener);
        findViewById(R.id.settingsButton).setOnTouchListener(listener);
        findViewById(R.id.facebookButton).setOnTouchListener(listener);
        findViewById(R.id.helpButton).setOnTouchListener(listener);
        findViewById(R.id.playButton).setOnTouchListener(listener);
        findViewById(R.id.cameraButton).setOnTouchListener(listener);
        findViewById(R.id.galleryButton).setOnTouchListener(listener);

        if(GameUtilities.GetGameLevel(getApplicationContext())==-1){
            GameUtilities.SetGameLevel(getApplicationContext(), 0);
        }
    }


    public void likeButtonClicked(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void settingsButtonClicked(){
        Intent intent = new Intent(this, LevelsActivity.class);
        startActivity(intent);
    }

    public void facebookButtonClicked(){
        String url="http://play.google.com/store/apps/details?id=" + getPackageName();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Image Puzzle game "+url);
        startActivity(Intent.createChooser(shareIntent, "Share Image Puzzle to.."));
    }

    public void helpButtonClicked() {
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    public void playButtonClicked(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("comingFrom","play");
        startActivity(intent);
    }

    public void cameraButtonClicked(){
        //http://www.edu4java.com/en/androidgame/androidgame2.html
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraSavedImagePath=Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"imagePuzzle_image.png"));
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraSavedImagePath);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE);
        }
    }

    public void galleryButtonClicked(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == SELECT_PICTURE || requestCode == CAMERA_CAPTURE) && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, GameActivity.class);
            if ( requestCode == SELECT_PICTURE) {
                intent.putExtra("comingFrom", "gallery");
                intent.putExtra("gameImage", data.getData().toString());
            } else if (requestCode == CAMERA_CAPTURE ) {
                intent.putExtra("comingFrom", "camera");
                intent.putExtra("gameImage",cameraSavedImagePath.toString());
            }
            startActivity(intent);
        }
    }

    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            boolean consumed = false;
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_fadeout));
            }
            else if(event.getAction()==MotionEvent.ACTION_UP)
            {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_fadein));
                switch (view.getId()) {
                    case R.id.likeButton:
                        likeButtonClicked();
                        consumed= true;
                        break;

                    case R.id.settingsButton:
                        settingsButtonClicked();
                        consumed = true;
                        break;

                    case R.id.facebookButton:
                        facebookButtonClicked();
                        consumed = true;
                        break;

                    case R.id.helpButton:
                        helpButtonClicked();
                        consumed = true;
                        break;

                    case R.id.playButton:
                        playButtonClicked();
                        consumed = true;
                        break;

                    case R.id.cameraButton:
                        cameraButtonClicked();
                        consumed = true;
                        break;

                    case R.id.galleryButton:
                        galleryButtonClicked();
                        consumed = true;
                        break;

                    default:
                        break;
                }
            }
            return consumed;
        }
    };

}

