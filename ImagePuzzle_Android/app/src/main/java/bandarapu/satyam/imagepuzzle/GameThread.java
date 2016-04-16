package bandarapu.satyam.imagepuzzle;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;

/**
 * Created by satyam on 2/1/16.
 */
class GameThread extends Thread {

    WeakReference<SurfaceHolder> mSurfaceHolder;
    WeakReference<GameView> canvas;
    private boolean running=false;
    private final static int SLEEP_TIME=40;

    public GameThread(GameView view) {
        super();
        canvas = new WeakReference<GameView>(view);
        mSurfaceHolder =  new WeakReference<SurfaceHolder>(view.getHolder());
    }

    public void startThread()
    {
        running = true;
        super.start();
    }

    public void stopThread()
    {
        running = false;
    }

    @Override
    public void run() {
        Log.v("SurfaceThread", "run method");
        Canvas c = null;
        while (running)
        {
            c = null;
            try
            {
                c = mSurfaceHolder.get().lockCanvas();
                synchronized (mSurfaceHolder.get())
                {
                    if (c != null)
                    {
                        canvas.get().draw(c);
                    }
                }
                sleep(SLEEP_TIME);
            }
            catch(InterruptedException ie)
            {
            }
            finally
            {
                // do this in a finally so that if an exception is thrown
                // we don't leave the Surface in an inconsistent state
                if (c != null)
                {
                    mSurfaceHolder.get().unlockCanvasAndPost(c);
                }
            }
        }
    }
}
