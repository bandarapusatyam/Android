package bandarapu.satyam.imagepuzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by satyam on 1/14/16.
 */
public class GameUtilities {

    //public static int Level1 = 2 * 2;
    //public static int Level2 = 2 * 3;
    //public static int Level3 = 2 * 4;
    //public static int Level4 = 3 * 4;
    //public static int Level5 = 4 * 4;
    //public static int Level6 = 5 * 4;
    //public static int Level7 = 5 * 5;
    //public static int Level8 = 5 * 6;

     static int[] rows = {
        2,2,2,3,4,5,5,5};

     static int[] cols = {
        2,3,4,4,4,4,5,6};

     static Uri gameImagePath=null;

    static final int GameStageReady = 1;
    static final int GameStageShuffle = 2;
    static final int GameStagePlay = 3;
    static final int GameStageFinished = 4;
    static final int GameStageError = 5;

    //public enum GameStage{ready, shuffle, play, finished, error};

    static DisplayMetrics getDeviceMetrics(WindowManager wmgr){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wmgr.getDefaultDisplay().getMetrics(displayMetrics);
        return  displayMetrics;
    }

    public static void SetGameLevel(Context context,int level)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(context.getResources().getString(R.string.game_level), level);
        editor.commit();
    }

    public static int GetGameLevel(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(context.getResources().getString(R.string.game_level), -1);
    }

    public static int GetGameRows(Context context)
    {
        return rows[GetGameLevel(context)];
    }

    public static int GetGameCols(Context context)
    {
       return cols[GetGameLevel(context)];
    }

    public static void SetGameImagePath(Uri imagePath){
        gameImagePath=imagePath;
    }

    public static Uri GetGameImagePath(){
        return gameImagePath;
    }
}
