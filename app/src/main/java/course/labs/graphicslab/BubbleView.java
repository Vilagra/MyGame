package course.labs.graphicslab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vilagra on 28.03.2017.
 */

public class BubbleView extends View {
    private Type type;
    private static final int BITMAP_SIZE = 64;
    private static final int REFRESH_RATE = 40;
    private final Paint mPainter = new Paint();
    private ScheduledFuture<?> mMoverFuture;
    private int mScaledBitmapWidth;
    private Bitmap mScaledBitmap;
    private TypeOfRemove typeOfRemove;
    BubleViewListener bubleViewListener;

    enum TypeOfRemove {FLINK,TOUCH,DOUBLETOUCH,MISSED,STOP}

    public void setTypeOfRemove(TypeOfRemove typeOfRemove) {
        this.typeOfRemove = typeOfRemove;
    }

    interface BubleViewListener{
        void handleViewRomoving(BubbleView bubbleView, TypeOfRemove typeRemove);

    }

    public void setBubleViewListener(BubleViewListener bubleViewListener) {
        this.bubleViewListener = bubleViewListener;
    }

    // location, speed and direction of the bubble
    private float mXPos, mYPos, mDx, mDy;
    private long mRotate, mDRotate;

    BubbleView(Context context, float x, float y) {
        super(context);
        this.type=type;
        // Create a new random number generator to
        // randomize size, rotation, speed and direction
        Random r = new Random();
        // Creates the bubble bitmap for this BubbleView
        createScaledBitmap(r);
        // Adjust position to center the bubble under user's finger
        mXPos = x;
        mYPos = y;
        // Set the BubbleView's speed and direction
        setSpeedAndDirection(r);
        // Set the BubbleView's rotation
        setRotation(r);
        mPainter.setAntiAlias(true);

    }

    public Type getType() {
        return type;
    }

    private void setRotation(Random r) {
        //mDRotate = level * 3;
    }

    private void setSpeedAndDirection(Random r) {
        // TODO - Set movement direction and speed
        // Limit movement speed in the x and y
        // direction to [-3..3] pixels per movement.
        mDx =  2* (r.nextInt(2) == 1 ? 1 : -1);
        mDy = 2 * (r.nextInt(2) == 1 ? 1 : -1);

    }

    private void createScaledBitmap(Random r) {
        mScaledBitmapWidth = BITMAP_SIZE * (r.nextInt(2) + 2);
        int rand = r.nextInt(8);
        switch (rand){
            case 0:
                type= Type.BELIK;
                break;
            case 1:case 2:case 3:case 4:
                type= Type.MEN;
                break;
            default:
                type= Type.WOMEN;
                break;
        }
        // TODO - create the scaled bitmap using size set above
        mScaledBitmap = Bitmap.createScaledBitmap(Data.bitmaps[rand], mScaledBitmapWidth, mScaledBitmapWidth, false);
    }

    // Start moving the BubbleView & updating the display
    public void start() {
        // Creates a WorkerThread
        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);
        // Execute the run() in Worker Thread every REFRESH_RATE
        // milliseconds
        // Save reference to this job in mMoverFuture
        mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (BubbleView.this.moveWhileOnScreen()) {
                    BubbleView.this.postInvalidate();
                } else {
                    BubbleView.this.postInvalidate();
                    if(typeOfRemove==null){
                        typeOfRemove=TypeOfRemove.MISSED;
                    }
                    BubbleView.this.stop();
                }
            }
        }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
    }

    // Returns true if the BubbleView intersects position (x,y)
    public synchronized boolean intersects(float x, float y) {
        // TODO - Return true if the BubbleView intersects position (x,y)
        if (x > mXPos && x < mXPos + mScaledBitmapWidth && y > mYPos && y < mYPos + mScaledBitmapWidth) {
            return true;
        }
        return false;
    }

    // Cancel the Bubble's movement
    // TypeOfRemove Bubble from mFrame
    // Play pop sound if the BubbleView was popped

    public void stop() {
        if (null != mMoverFuture && !mMoverFuture.isDone()) {
            mMoverFuture.cancel(true);
        }
        if(typeOfRemove!=TypeOfRemove.STOP) {
            bubleViewListener.handleViewRomoving(this, typeOfRemove);
        }
        // This work will be performed on the UI Thread

    }

    // Change the Bubble's speed and direction
    public synchronized void deflect(float velocityX, float velocityY) {
        mDx = velocityX / REFRESH_RATE;
        mDy = velocityY / REFRESH_RATE;
    }

    // Draw the Bubble at its current location
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDRotate, mXPos, mYPos);
        canvas.drawBitmap(mScaledBitmap, mXPos, mYPos, mPainter);
        canvas.restore();

    }

    // Returns true if the BubbleView is still on the screen after the move
    // operation
    private synchronized boolean moveWhileOnScreen() {
        if (!isOutOfView()) {
            mXPos += mDx;
            mYPos += mDy;
            return true;
        }
        return false;
    }

    // Return true if the BubbleView is off the screen after the move
    // operation
    private boolean isOutOfView() {
        if (mYPos < 0 - mScaledBitmapWidth
                || mYPos > Data.getmDisplayHeight()
                || mXPos < 0 - mScaledBitmapWidth
                || mXPos > Data.getmDisplayWidth()) {
            return true;
        } else {
            return false;
        }

    }
}
