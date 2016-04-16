package bandarapu.satyam.imagepuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by satyam on 1/15/16.
 */
public class GameEngine {
    private Context mContext;
    WeakReference <GameActivity> gameActivity;
    private Bitmap mOriginalImage=null;
    private Bitmap[] splitImages;
    private int[] imagePositions;
    private  Rect[] splitArea;
    private int rows;
    private int cols;

    private int gameStage;
    private int tappedOnImagePos;
    private int releasedOnImagePos;
    private boolean draggingStarted;
    private boolean draggingReleased;
    private int currentPointerX, currentPointerY;
    private int tappedWidth, tappedHeight;

    private int width;
    public GameEngine( GameActivity activity,Context context){
        mContext=context;
        gameActivity= new WeakReference<GameActivity>(activity);
    }

    public void setCurrentPointerPos(int x, int y){
        currentPointerX = x;
        currentPointerY=y;
    }

    public void setDistanceFromTappedPos(int x, int y){
        tappedWidth = x-splitArea[tappedOnImagePos].left;
        tappedHeight = y-splitArea[tappedOnImagePos].top;
    }

    public void setTappedImagePosition(int pos){
        tappedOnImagePos = pos;
    }

    public void setReleasedOnImagePosition(int pos){
        releasedOnImagePos = pos;
    }

    public void setDraggingStarted(boolean started){
        draggingStarted=started;
    }

    public void setDraggingReleased(boolean released){
        draggingReleased=released;
    }

    public void setGameStage(int aStage){
        this.gameStage=aStage;
    }

    public int getGameStage(){
        return gameStage;
    }

    public void setGameImage(Bitmap bitmap){
        mOriginalImage=bitmap;
    }

    public Bitmap getGameImage(){
        return mOriginalImage;
    }

    public void LoadImageAndSplit()
    {
        gameStage= GameUtilities.GameStageReady;
        rows = GameUtilities.GetGameRows(mContext);
        cols = GameUtilities.GetGameCols(mContext);
        DisplayMetrics dm=mContext.getResources().getDisplayMetrics();
        int chunkWidth=dm.widthPixels/cols;
        int chunkHeight = dm.heightPixels/rows;
        width = dm.widthPixels;


        //TODO: out of memory issue needs to handle.
        try {
            mOriginalImage = Bitmap.createScaledBitmap(mOriginalImage, dm.widthPixels, dm.heightPixels, true);
        } catch (Exception ex){
            setGameStage(GameUtilities.GameStageError);
            gameActivity.get().DisplayErrorMessage();
        }
        splitArea = new Rect[rows * cols];
        splitImages = new Bitmap[rows * cols];
        int pos = -1;

        int yCoord = 0;
        for(int x=0; x<rows; x++){
            int xCoord = 0;
            for(int y=0; y<cols; y++){
                pos = pos + 1;
                splitArea[pos] = new Rect(xCoord, yCoord, xCoord+chunkWidth, yCoord+chunkHeight);
                splitImages[pos] = Bitmap.createBitmap(mOriginalImage, xCoord, yCoord, chunkWidth, chunkHeight);
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        imagePositions = new int[cols * rows];
        for (int i = 0; i < cols * rows; i++)
        {
            imagePositions[i] = i;
        }
    }

    public void StartShuffle()
    {
        boolean randomFinished = false;
        while (!randomFinished)
        {   DoShuffle();
            for (int i = 0; i < imagePositions.length; i++)
            {
                if (i != imagePositions[i])
                {
                    randomFinished = true;
                    break;
                }
            }
        }
    }

    public void DoShuffle()
    {
        Random random = new Random(System.currentTimeMillis());
        for (int i = imagePositions.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            // Simple swap
            int a = imagePositions[index];
            imagePositions[index] = imagePositions[i];
            imagePositions[i] = a;
        }
    }

    public int getImagePos(int x, int y) {
        int pos = 0;
        for (int i = 0; i < splitArea.length; i++) {
            if (x >= splitArea[i].left
                    && x <= splitArea[i].left + splitArea[i].width()
                    && y >= splitArea[i].top
                    && y <= splitArea[i].top + splitArea[i].height()) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public boolean CheckGameOver()
    {
        boolean status = true;
        for (int i = 0; i < GameUtilities.GetGameRows(mContext) * GameUtilities.GetGameCols(mContext); i++)
        {
            if (imagePositions[i] != i)
            {
                status = false;
                break;
            }
        }
        return status;
    }

    void displayGameOver(){
        gameActivity.get().DisplayGameOver();
    }

    public void drawGame(Canvas canvas){
        if(getGameStage()== GameUtilities.GameStageReady ||
                getGameStage()== GameUtilities.GameStageShuffle ||
                getGameStage()== GameUtilities.GameStageFinished ) {
            for (int i = 0; i < rows * cols; i++) {
                canvas.drawBitmap(splitImages[imagePositions[i]],
                        splitArea[i].left, splitArea[i].top, null);
            }

            if (getGameStage() == GameUtilities.GameStageReady) {
                Paint paint = new Paint();
                paint.setColor(android.R.color.holo_orange_dark | Color.GREEN);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextSize(100);
                if(width - 100 >10)
                    width = width-100;
                else
                    width=10;
                canvas.drawText("Touch to split the image", width, mContext.getResources().getDisplayMetrics().heightPixels / 2 + 15, paint);
            } else if (getGameStage() == GameUtilities.GameStageShuffle) {
                setGameStage(GameUtilities.GameStagePlay);
            }
        }else if(getGameStage()== GameUtilities.GameStagePlay){
            for (int i = 0; i < rows * cols; i++) {
                if (!draggingStarted) {
                    canvas.drawBitmap(splitImages[imagePositions[i]],
                            splitArea[i].left, splitArea[i].top, null);
                } else if (tappedOnImagePos != i) {
                    canvas.drawBitmap(splitImages[imagePositions[i]],
                            splitArea[i].left, splitArea[i].top, null);
                }
            }

            if (draggingStarted) {
                if (draggingReleased) {
                    canvas.drawBitmap(splitImages[imagePositions[tappedOnImagePos]],
                            splitArea[releasedOnImagePos].left,
                            splitArea[releasedOnImagePos].top, null);
                    canvas.drawBitmap(splitImages[imagePositions[releasedOnImagePos]],
                            splitArea[tappedOnImagePos].left, splitArea[tappedOnImagePos].top, null);
                    draggingStarted = false;
                    draggingReleased = false;
                    int a = imagePositions[tappedOnImagePos];
                    imagePositions[tappedOnImagePos] = imagePositions[releasedOnImagePos];
                    imagePositions[releasedOnImagePos] = a;
                    if (CheckGameOver()) {
                        setGameStage(GameUtilities.GameStageFinished);
                        for (int i = 0; i < rows * cols; i++) {
                            canvas.drawBitmap(splitImages[i],
                                    splitArea[i].left, splitArea[i].top, null);
                        }
                        displayGameOver();
                    }
                } else {
                    canvas.drawBitmap(splitImages[imagePositions[tappedOnImagePos]],
                            currentPointerX - tappedWidth,
                            currentPointerY  - tappedHeight, null);
                }
            }
        }
    }
}
