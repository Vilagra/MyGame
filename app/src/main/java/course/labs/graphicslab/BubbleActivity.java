package course.labs.graphicslab;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BubbleActivity extends Activity implements View.OnClickListener, BubbleView.BubleViewListener {

    // These variables are for testing purposes, do not modify

    //private static int speedMode = RANDOM;
    LogicOfGame logicOfGame;


    // The Main view
    private RelativeLayout mFrame;
    private TextView levelTextView;
    private TextView scoreTextView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Button mButton;

    String stringForScore;
    String stringForMissed;
    String stringForLevel;

    private ScheduledFuture<?> mCreator;

    // AudioManager
    private AudioManager mAudioManager;
    // SoundPool
    private SoundPool mSoundPool;
    // ID for the bubble popping sound
    private int mSoundPop;
    private int mSoundMissed;
    private int mSoundFlink;
    private int mSoundLoose;
    private int mSoundWin;
    private int mSoundBelik;
    // Audio volume
    private float mStreamVolume;

    // Gesture Detector
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Data.initialize(getApplicationContext());
        logicOfGame = LogicOfGame.getLogicOfGame();
        // Set up user interface
        mFrame = (RelativeLayout) findViewById(R.id.frame);
        levelTextView = (TextView) findViewById(R.id.level);
        scoreTextView = (TextView) findViewById(R.id.score);
        imageView1= (ImageView) findViewById(R.id.missed1);
        imageView2= (ImageView) findViewById(R.id.missed2);
        imageView3= (ImageView) findViewById(R.id.missed3);
        mButton = (Button) findViewById(R.id.start);
        mButton.setOnClickListener(this);
        stringForLevel = getString(R.string.level);
        stringForMissed = getString(R.string.missed);
        stringForScore = getString(R.string.score);
        updateView();
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
        mSoundLoose = mSoundPool.load(getApplicationContext(), R.raw.uauauauaaa, 1);
        mSoundWin = mSoundPool.load(getApplicationContext(), R.raw.harosh, 1);
        mSoundBelik = mSoundPool.load(getApplicationContext(), R.raw.suchka, 1);

    }

    private void updateView(){
        mButton.setVisibility(View.VISIBLE);
        levelTextView.setVisibility(View.VISIBLE);
        levelTextView.setText(stringForLevel + " "+logicOfGame.getCurrentLevel());
        scoreTextView.setText(stringForScore + " "+logicOfGame.getScore());
        imageView1.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView3.setVisibility(View.VISIBLE);
        //missedTextView.setText(stringForMissed + " "+logicOfGame.getMissed());
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
                                if (buble.intersects(x, y) && buble.getType() == Type.WOMEN) {
                                    buble.setTypeOfRemove(BubbleView.TypeOfRemove.FLINK);
                                    buble.deflect(velocityX, velocityY);
                                    return true;
                                }
                            }

                        }
                        return true;

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
                                if (buble.intersects(x, y) && buble.getType() == Type.MEN) {
                                    buble.setTypeOfRemove(BubbleView.TypeOfRemove.TOUCH);
                                    buble.stop();
                                    return true;
                                }
                            }
                        }
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        float x = e.getX();
                        float y = e.getY();
                        for (int i = 0; i < mFrame.getChildCount(); i++) {
                            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                                BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                                if (buble.intersects(x, y) && buble.getType() == Type.BELIK) {
                                    buble.setTypeOfRemove(BubbleView.TypeOfRemove.DOUBLETOUCH);
                                    buble.stop();
                                    return true;
                                }
                            }
                        }
                        return true;
                    }
                }

        );

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
        levelTextView.setVisibility(View.GONE);
        genarateView();
    }

    public void genarateView(){
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
        }, 0, 1000 - logicOfGame.getSpeed(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void handleViewRomoving(final BubbleView bubbleView, final BubbleView.TypeOfRemove typeOfRemove) {
        mFrame.post(new Runnable() {
            @Override
            public void run() {
                mFrame.removeView(bubbleView);
                setSoundAndText(typeOfRemove);
                if (logicOfGame.getResult() != null) {
                    if (null != mCreator && !mCreator.isDone()) {
                        stopGame();
                    }
                }

            }
        });
    }

    private void setSoundAndText(BubbleView.TypeOfRemove typeOfRemove) {
        switch (typeOfRemove) {
            case TOUCH:
                mSoundPool.play(mSoundPop, 0.5f, 0.5f, 0, 0, 1.0f);
                logicOfGame.increaseScore(2);
                scoreTextView.setText(stringForScore +" "+ logicOfGame.getScore());
                break;
            case FLINK:
                mSoundPool.play(mSoundFlink, 0.5f, 0.5f, 0, 0, 1.0f);
                logicOfGame.increaseScore(3);
                scoreTextView.setText(stringForScore + " "+logicOfGame.getScore());
                break;
            case MISSED:
                mSoundPool.play(mSoundMissed, 0.5f, 0.5f, 0, 0, 1.0f);
                logicOfGame.increaseMissed();
                switch (logicOfGame.getMissed()){
                    case 1:
                        imageView1.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        imageView2.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        imageView3.setVisibility(View.INVISIBLE);
                        break;
                }
                break;
            case DOUBLETOUCH:
                mSoundPool.play(mSoundBelik, 0.5f, 0.5f, 0, 0, 1.0f);
                logicOfGame.increaseScore(4);
                scoreTextView.setText(stringForScore + " "+logicOfGame.getScore());
                break;
        }
    }


    private void stopGame() {
        if (null != mCreator && !mCreator.isDone()) {
            mCreator.cancel(true);
        }
        stopView();
        if (logicOfGame.getResult() == LogicOfGame.Result.WIN) {
            logicOfGame.resetForNextLevel();
            logicOfGame.increaseLevel();
           // mButton.setVisibility(View.VISIBLE);
            //levelTextView.setVisibility(View.VISIBLE);
            //levelTextView.setText(stringForLevel + " " + logicOfGame.getCurrentLevel());
            cleanFrame();
            updateView();
            mSoundPool.play(mSoundWin, 0.5f, 0.5f, 0, 0, 1.0f);
        }
        if (logicOfGame.getResult() == LogicOfGame.Result.LOSE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("LOOSER !!!").
                    setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            cleanFrame();
                            logicOfGame.resetForNewGame();
                            updateView();
                        }
                    }).
                    setIcon(getResources().getDrawable(R.drawable.belik)).
                    setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.create().show();
            mSoundPool.play(mSoundLoose, 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }

    private void stopView() {
        for (int i = 0; i < mFrame.getChildCount(); i++) {
            int j = mFrame.getChildCount();
            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                final BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                buble.setTypeOfRemove(BubbleView.TypeOfRemove.STOP);
                buble.stop();
            }
        }
    }


    private void startView() {
        for (int i = 0; i < mFrame.getChildCount(); i++) {
            //int j = mFrame.getChildCount();
            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                final BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                buble.setTypeOfRemove(null);
                buble.start();
            }
        }
    }

    private void cleanFrame() {
        for (int i = 0; i < mFrame.getChildCount(); i++) {
            if (mFrame.getChildAt(i).getClass() == BubbleView.class) {
                final BubbleView buble = (BubbleView) mFrame.getChildAt(i);
                mFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        mFrame.removeView(buble);
                    }
                });

            }
        }
    }

    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other actions.
    // A new BubbleView is created for each bubble on the displa


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_still_mode:
                stopGame();
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog(){
        final Dialog d = new Dialog(this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.level_picker);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(20);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                logicOfGame.setCurrentLevel(np.getValue());
                cleanFrame();
                logicOfGame.resetForNextLevel();
                updateView();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startView();
                genarateView();
                d.dismiss();
            }
        });
        d.show();

    }

    private void exitRequested() {
        super.onBackPressed();
    }
}