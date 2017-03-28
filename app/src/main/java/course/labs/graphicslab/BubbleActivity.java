package course.labs.graphicslab;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BubbleActivity extends Activity implements View.OnClickListener {

    // These variables are for testing purposes, do not modify

    //private static int speedMode = RANDOM;
    int level = 1;


    // The Main view
    private RelativeLayout mFrame;
    private TextView mTextView;
    private Button mButton;

    private ScheduledFuture<?> mCreator;

    // Display dimensions
    private int mDisplayWidth, mDisplayHeight;

    // Sound variables

    // AudioManager
    private AudioManager mAudioManager;
    // SoundPool
    private SoundPool mSoundPool;
    // ID for the bubble popping sound
    private int mSoundPop;
    private int mSoundMissed;
    private int mSoundFlink;
    // Audio volume
    private float mStreamVolume;
    Bitmap[] bitmaps = new Bitmap[9];


    // Gesture Detector
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // Set up user interface
        mFrame = (RelativeLayout) findViewById(R.id.frame);
        mTextView = (TextView) findViewById(R.id.level);
        mButton = (Button) findViewById(R.id.start);
        mButton.setOnClickListener(this);


        // Load basic bubble Bitmap
        bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.belik);
        bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.roman);
        bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.levenko);
        bitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.oleg);
        bitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.djonni);
        bitmaps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.jana);
        bitmaps[6] = BitmapFactory.decodeResource(getResources(), R.drawable.sveta);
        bitmaps[7] = BitmapFactory.decodeResource(getResources(), R.drawable.lena);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Manage bubble popping sound
        // Use AudioManager.STREAM_MUSIC as stream type

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mStreamVolume = (float) mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC)
                / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //mStreamVolume = (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // TODO - make a new SoundPool, allowing up to 10 streams
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // TODO - set a SoundPool OnLoadCompletedListener that calls setupGestureDetector()
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                setupGestureDetector();
            }
        });

        // TODO - load the sound from res/raw/bubble_pop.wav
        mSoundPop = mSoundPool.load(getApplicationContext(), R.raw.bubble_pop, 1);
        mSoundMissed = mSoundPool.load(getApplicationContext(), R.raw.oyo, 1);
        mSoundFlink = mSoundPool.load(getApplicationContext(), R.raw.yohoo, 1);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
            mDisplayWidth = mFrame.getWidth();
            mDisplayHeight = mFrame.getHeight();
        }
    }

    // Set up GestureDetector
    private void setupGestureDetector() {

        mGestureDetector = new GestureDetector(this,

                new GestureDetector.SimpleOnGestureListener() {

                    // If a fling gesture starts on a BubbleView then change the
                    // BubbleView's velocity

                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2,
                                           float velocityX, float velocityY) {

                        // TODO - Implement onFling actions.
                        // You can get all Views in mFrame using the
                        // ViewGroup.getChildCount() method
                        float x = event1.getX();
                        float y = event1.getY();
                        for (int i = 0; i < mFrame.getChildCount(); i++) {
                            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                                BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                                if (buble.intersects(x, y)&&buble.type==Type.WOMEN) {
                                    buble.deflect(velocityX, velocityY);
                                    return true;
                                }
                            }

                        }
                        return false;

                    }

                    // If a single tap intersects a BubbleView, then pop the BubbleView
                    // Otherwise, create a new BubbleView at the tap's location and add
                    // it to mFrame. You can get all views from mFrame with ViewGroup.getChildAt()

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent event) {

                        // TODO - Implement onSingleTapConfirmed actions.
                        // You can get all Views in mFrame using the
                        // ViewGroup.getChildCount() method
                        float x = event.getX();
                        float y = event.getY();
                        for (int i = 0; i < mFrame.getChildCount(); i++) {
                            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                                BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                                if (buble.intersects(x, y)&&buble.type==Type.MEN) {
                                    buble.stop(true);
                                    return true;
                                }
                            }
                        }
                        //final BubbleView bubbleView = new BubbleView(getApplicationContext(), x, y);
                        //mFrame.addView(bubbleView);


                        return true;
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // TODO - Delegate the touch to the gestureDetector

        mGestureDetector.onTouchEvent(event);
        return false;

    }

    @Override
    protected void onPause() {

        // TODO - Release all SoundPool resources
        mSoundPool.release();

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        mButton.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);
        final Random r = new Random();
        mCreator = executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                final int x = r.nextInt(mDisplayWidth - 188) + 60;
                final int y = r.nextInt(mDisplayHeight - 188) + 60;
                new BubbleView(getApplicationContext(), x, y);
                mFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        BubbleView bubbleView = new BubbleView(getApplicationContext(), x, y);
                        mFrame.addView(bubbleView);
                        bubbleView.start();
                    }
                });
            }
        }, 0, 750, TimeUnit.MILLISECONDS);
    }

    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other actions.
    // A new BubbleView is created for each bubble on the displa
    enum Type{
        MEN,WOMEN,BELIK
    }

    public class BubbleView extends View {
        private Type type;
        private static final int BITMAP_SIZE = 64;
        private static final int REFRESH_RATE = 40;
        private final Paint mPainter = new Paint();
        private ScheduledFuture<?> mMoverFuture;
        private int mScaledBitmapWidth;
        private Bitmap mScaledBitmap;
        boolean wasFlink;

        // location, speed and direction of the bubble
        private float mXPos, mYPos, mDx, mDy, mRadius, mRadiusSquared, mRadiusAdj;
        private long mRotate, mDRotate;

        BubbleView(Context context, float x, float y) {
            super(context);
            this.type=type;
            // Create a new random number generator to
            // randomize size, rotation, speed and direction
            Random r = new Random();
            // Creates the bubble bitmap for this BubbleView
            createScaledBitmap(r);
            // Radius of the Bitmap
            mRadiusAdj = mRadius + 20;
            // Adjust position to center the bubble under user's finger
            mXPos = x;
            mYPos = y;
            // Set the BubbleView's speed and direction
            setSpeedAndDirection(r);
            // Set the BubbleView's rotation
            setRotation(r);
            mPainter.setAntiAlias(true);
        }

        private void setRotation(Random r) {
            mDRotate = level * 3;
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
                    type=Type.MEN;
                    break;
                case 1:case 2:case 3:case 4:
                    type=Type.MEN;
                    break;
                default:
                    type=Type.WOMEN;
                    break;
            }
            // TODO - create the scaled bitmap using size set above
            mScaledBitmap = Bitmap.createScaledBitmap(bitmaps[rand], mScaledBitmapWidth, mScaledBitmapWidth, false);
        }

        // Start moving the BubbleView & updating the display
        private void start() {

            // Creates a WorkerThread
            ScheduledExecutorService executor = Executors
                    .newScheduledThreadPool(1);

            // Execute the run() in Worker Thread every REFRESH_RATE
            // milliseconds
            // Save reference to this job in mMoverFuture
            mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {

                    // TODO - implement movement logic.
                    // Each time this method is run the BubbleView should
                    // move one step. If the BubbleView exits the display,
                    // stop the BubbleView's Worker Thread.
                    // Otherwise, request that the BubbleView be redrawn.
                    if (BubbleView.this.moveWhileOnScreen()) {
                        BubbleView.this.postInvalidate();
                    } else {
                        BubbleView.this.postInvalidate();
                        BubbleView.this.stop(false);
                    }
                }
            }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
        }

        // Returns true if the BubbleView intersects position (x,y)
        private synchronized boolean intersects(float x, float y) {
            // TODO - Return true if the BubbleView intersects position (x,y)
            if (x > mXPos && x < mXPos + mScaledBitmapWidth && y > mYPos && y < mYPos + mScaledBitmapWidth) {
                return true;
            }
            return false;
        }

        // Cancel the Bubble's movement
        // Remove Bubble from mFrame
        // Play pop sound if the BubbleView was popped

        private void stop(final boolean wasPopped) {

            if (null != mMoverFuture && !mMoverFuture.isDone()) {
                mMoverFuture.cancel(true);
            }

            // This work will be performed on the UI Thread
            mFrame.post(new Runnable() {
                @Override
                public void run() {
                    mFrame.removeView(BubbleView.this);
                    if (wasPopped) {
                        mSoundPool.play(mSoundPop, 0.5f, 0.5f, 0, 0, 1.0f);
                    } else if (wasFlink) {
                        mSoundPool.play(mSoundFlink, 0.5f, 0.5f, 0, 0, 1.0f);
                    } else {
                        mSoundPool.play(mSoundMissed, 0.5f, 0.5f, 0, 0, 1.0f);
                    }
                }
            });
        }

        // Change the Bubble's speed and direction
        private synchronized void deflect(float velocityX, float velocityY) {
            wasFlink = true;
            mDx = velocityX / REFRESH_RATE;
            mDy = velocityY / REFRESH_RATE;
        }

        // Draw the Bubble at its current location
        @Override
        protected synchronized void onDraw(Canvas canvas) {
            canvas.save();
            canvas.rotate(mDRotate, mXPos + mRadius, mYPos + mRadius);
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
                    || mYPos > mDisplayHeight
                    || mXPos < 0 - mScaledBitmapWidth
                    || mXPos > mDisplayWidth) {
                return true;
            } else {
                return false;
            }

        }
    }

    // Do not modify below here

    @Override
    public void onBackPressed() {
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_still_mode:
                speedMode = STILL;
                return true;
            case R.id.menu_single_speed:
                speedMode = SINGLE;
                return true;
            case R.id.menu_random_mode:
                speedMode = RANDOM;
                return true;
            case R.id.quit:
                exitRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

    private void exitRequested() {
        super.onBackPressed();
    }
}