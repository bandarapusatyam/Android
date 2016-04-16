package bandarapu.satyam.imagepuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LevelsActivity extends Activity {

    GridView gridView;
    int previousSelectedLevel=0;
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

        setContentView(R.layout.activity_levels);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new LevelsImageAdapter(getApplicationContext()));
        previousSelectedLevel = GameUtilities.GetGameLevel(getApplicationContext());
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(previousSelectedLevel!=position) {
                    setLevelSelected(position);
                }
            }
        });
    }

    private void setLevelSelected(int position){
        ImageView imageView = (ImageView) gridView.getChildAt(position);
        imageView.setImageResource(R.drawable.selectedlevel);
        ImageView preImageView = (ImageView) gridView.getChildAt(previousSelectedLevel);
        preImageView.setImageBitmap(null);
        previousSelectedLevel = position;
        GameUtilities.SetGameLevel(getApplicationContext(),position);
    }
}
