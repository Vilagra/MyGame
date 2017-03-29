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
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BubbleActivity extends Activity implements View.OnClickListener,BubbleView.BubleViewListener {

    // These variables are for testing purposes, do not modify

    //private static int speedMode = RANDOM;
    int level = 1;


    // The Main view
    private RelativeLayout mFrame;
    private TextView mTextView;
    private Button mButton;

    private ScheduledFuture<?> mCreator;

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

    // Gesture Detector
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        Data.initialize(getApplicationContext());
        // Set up user interface
        mFrame = (RelativeLayout) findViewById(R.id.frame);
        mTextView = (TextView) findViewById(R.id.level);
        mButton = (Button) findViewById(R.id.start);
        mButton.setOnClickListener(this);
        // Load basic bubble Bitmap


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Manage bubble popping sound
        // Use Audioanager.STREAM_MUSIC as stream type
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mStreamVolume = (float) mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC)
                / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);


        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                setupGestureDetector();
            }
        });

        mSoundPop = mSoundPool.load(getApplicationContext(), R.raw.bubble_pop, 1);
        mSoundMissed = mSoundPool.load(getApplicationContext(), R.raw.oyo, 1);
        mSoundFlink = mSoundPool.load(getApplicationContext(), R.raw.yohoo, 1);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
            Data.setmDisplayWidth(mFrame.getWidth());
            Data.setmDisplayHeight(mFrame.getHeight());
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
                        float x = event1.getX();
                        float y = event1.getY();
                        for (int i = 0; i < mFrame.getChildCount(); i++) {
                            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                                BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                                if (buble.intersects(x, y)&&buble.getType()==Type.WOMEN) {
                                    buble.deflect(velocityX, velocityY);
                                    return true;
                                }
                            }

                        }
                        return false;

                    }
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
                                if (buble.intersects(x, y)&&buble.getType()==Type.MEN) {
                                    buble.stop(true);
                                    return true;
                                }
                            }
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;

    }

    @Override
    protected void onPause() {

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
                final int x = r.nextInt(Data.getmDisplayWidth() - 252) + 60;
                final int y = r.nextInt(Data.getmDisplayHeight() - 252) + 60;
                mFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        BubbleView bubbleView = new BubbleView(getApplicationContext(), x, y);
                        bubbleView.setBubleViewListener(BubbleActivity.this);
                        mFrame.addView(bubbleView);
                        bubbleView.start();
                    }
                });
            }
        }, 0, 750, TimeUnit.MILLISECONDS);
    }

    @Override
    public void removeView(final BubbleView bubbleView, final boolean wasFlink, final boolean wasPopped) {
        mFrame.post(new Runnable() {
            @Override
            public void run() {
                mFrame.removeView(bubbleView);
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

    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other actions.
    // A new BubbleView is created for each bubble on the displa
    enum Type{
        MEN,WOMEN,BELIK
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