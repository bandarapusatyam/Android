package bandarapu.satyam.imagepuzzle;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Created by satyam on 1/14/16.
 */
public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    private GameActivity gameActivity;
    private Button replay, next,home;

    public CustomDialog(GameActivity activity){
        super(activity);
        this.gameActivity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        getWindow().setLayout(GameUtilities.getDeviceMetrics(gameActivity.getWindowManager()).widthPixels,
                GameUtilities.getDeviceMetrics(gameActivity.getWindowManager()).heightPixels);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_custom);

        replay = (Button) findViewById(R.id.btn_replay);
        next = (Button) findViewById(R.id.btn_next);
        home = (Button) findViewById(R.id.btn_home);
        replay.setOnClickListener(this);
        next.setOnClickListener(this);
        home.setOnClickListener(this);

        replay.startAnimation(AnimationUtils.loadAnimation(gameActivity.getApplicationContext(), R.anim.puzzle_animation));
        next.startAnimation(AnimationUtils.loadAnimation(gameActivity.getApplicationContext(), R.anim.dialog_animation));
        home.startAnimation(AnimationUtils.loadAnimation(gameActivity.getApplicationContext(), R.anim.dialog_animation));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_replay:
                gameActivity.prepareGameView();
                break;
            case R.id.btn_next:
                GameUtilities.SetGameLevel(gameActivity.getApplicationContext(),GameUtilities.GetGameLevel(gameActivity.getApplicationContext())+1);
                gameActivity.prepareGameView();
                break;
            case R.id.btn_home:
                gameActivity.stopGameThread();
                gameActivity.finish();
                break;
            default:
                break;
        }
        dismiss();
    }
}
