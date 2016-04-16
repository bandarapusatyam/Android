package bandarapu.satyam.imagepuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by bandarapu on 09/01/16.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback{


    private GameThread thread;
    private GameEngine gameEngine;
    public GameView(Context context) {
        super(context);
        // register our interest in hearing about changes to our surface
        getHolder().addCallback(this);
    }

    public void startGame()
    {
        if (thread == null)
        {
            thread = new GameThread(this);
        }
        thread.startThread();
    }

    public void stopGame()
    {
        if (thread != null)
        {
            thread.stopThread();

            // Waiting for the thread to die by calling thread.join,
            // repeatedly if necessary
            boolean retry = true;
            while (retry)
            {
                try
                {
                    thread.join();
                    retry = false;
                }
                catch (InterruptedException e)
                {
                }
            }
            thread = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        startGame();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
       stopGame();
    }

    public void startEngine(GameActivity activity,Context context,Bitmap bitmap){
        gameEngine = new GameEngine(activity,context);
        gameEngine.setGameImage(bitmap);
        prepareGameEngine();
    }

    public void prepareGameEngine(){
        gameEngine.LoadImageAndSplit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action==MotionEvent.ACTION_MOVE){
            if(gameEngine.getGameStage()== GameUtilities.GameStagePlay) {
                gameEngine.setCurrentPointerPos((int)event.getX(),(int)event.getY());
                gameEngine.setDraggingStarted(true);
            }
        }
        else if (action==MotionEvent.ACTION_DOWN){
            if(gameEngine.getGameStage()== GameUtilities.GameStagePlay){
                gameEngine.setTappedImagePosition(gameEngine.getImagePos((int) event.getX(), (int) event.getY()));
                gameEngine.setDistanceFromTappedPos((int)event.getX(),(int)event.getY());
            }
        }
        else if (action==MotionEvent.ACTION_UP){
            if(gameEngine.getGameStage()== GameUtilities.GameStageReady){
                gameEngine.StartShuffle();
                gameEngine.setGameStage(GameUtilities.GameStageShuffle);
            } else if(gameEngine.getGameStage()== GameUtilities.GameStagePlay){
                gameEngine.setReleasedOnImagePosition(gameEngine.getImagePos((int)event.getX(),(int)event.getY()));
                gameEngine.setDraggingReleased(true);
            }
        }
        return true;
    }

    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        gameEngine.drawGame(canvas);
    }
}
