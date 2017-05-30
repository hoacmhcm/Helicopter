package hoa14110071.chieuthusau.helicopter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by minhh on 28-May-17.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private Background background;
    private Player player;
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVE_SPEED = -5;
    public static final int DELAY_ANIMATION_PLAYER_MS = 100;

    private long smokeStartTime;
    private ArrayList<Smoke> smoke;

    private long missileStartTime;
    private ArrayList<Missile> missiles;

    private Random rand = new Random();
    private int level;

    private boolean newGameCreated;
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean gameStarted;
    private boolean collision = false;
    private int currentHighScore;
    private static final int SCORE_BOOSTER = 3;

    // Listener interface for hosting activity to save score in shared preferences
    public interface HighScoreListener {
        public void onHighScoreUpdated(int best);
    }

    private HighScoreListener mHighScoreListener;

    public void setHighScoreListener(HighScoreListener listener) {
        this.mHighScoreListener = listener;
    }

    public GamePanel(Context context, int currentHighScore) {
        super(context);

        this.currentHighScore = currentHighScore;

        // set null listener... just in case
        this.mHighScoreListener = null;


        //thêm callback vào SurfaceHolder để sinh ra các sự kiện
        getHolder().addCallback(this);
        //make gamePanel focusable so it can handle events onTouch
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //create background
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        //create player
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        player.resetScore();

        //create smoke
        smoke = new ArrayList<Smoke>();
        smokeStartTime = System.nanoTime();

        //create missile
        missiles = new ArrayList<>();
        missileStartTime = System.nanoTime();
        level = 1;


        //safely start the game loop
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter++ < 10000) {
//            System.out.println(counter);
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.isPlaying() && newGameCreated && reset) {
                player.setPlaying(true);
                player.setUp(true);
            }
            if (player.isPlaying()) {

                if (!gameStarted)
                    gameStarted = true;

                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {
        if (player.isPlaying()) {
            background.update();
            player.update();


            //add smoke puffs when it is time for a new one
            long elapsedTimeSmokeMS = (System.nanoTime() - smokeStartTime) / 1000000;
            if (elapsedTimeSmokeMS > 120) {
                smoke.add(new Smoke(player.getLeft(), player.getTop() + 10));
                smokeStartTime = System.nanoTime();
            }


            for (int i = 0; i < smoke.size(); i++) {
                smoke.get(i).update();
                if (smoke.get(i).getLeft() < -10) {
                    smoke.remove(i);
                }
            }

            //add new missiles when it is time for a new one
            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if (missileElapsed > (2000 - player.getScore() / 4)) {
                int mX = WIDTH + 10;
                int mY;

//                if (missiles.size() == 0)
//                    mY = HEIGHT / 2;   //first missile always goes down the middle

                // Add new missile and reset timer
                if (player.getScore() >= level * 100) {
                    System.out.println(level);
                    level++;
                    for (int i = 0; i < level; i++) {
                        mY = (int) (rand.nextDouble() * HEIGHT);
                        missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), mX, mY, 45, 15, player.getScore(), 13));
                    }
                } else {
                    for (int i = 0; i < level; i++) {
                        mY = (int) (rand.nextDouble() * HEIGHT);
                        missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), mX, mY, 45, 15, player.getScore(), 13));
                    }
                }
//                System.out.println(missiles.size());

//                int missileStreamId = mSoundPool.play(mp3Missile.getSoundId(), .20f, .20f, 1, PLAY_ONCE, 0.8f);
//                missiles.get(missiles.size()-1).setStreamId(missileStreamId);
                missileStartTime = System.nanoTime();
            }

            //loop through every missiles. Check collision and cleanup
            for (int i = 0; i < missiles.size(); i++) {
                //update each missile position and image
                missiles.get(i).update();

                if (collision(missiles.get(i), player)) {
//                    Log.i(TAG, "update: collision missile " + i);
//                    mSoundPool.stop(missiles.get(i).getStreamId());
                    missiles.remove(i);
                    player.setPlaying(false);
                    collision = true;
                    break;
                }

                //remove missile if it is way off the screen
                if (missiles.get(i).getLeft() < -100) {
//                    mSoundPool.stop(missiles.get(i).getStreamId());
                    missiles.remove(i);
//                    Log.i(TAG, "update: removing missile "+i);
                }
            }
        } else {
            player.resetDY();
            if(!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getLeft(),
                        player.getTop()-30, 100, 100, 25);

                if (collision) {
                    collision=false;
                }
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated) {
                newGame();
            }
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
        if (canvas != null) {
            //Saves the current matrix and clip onto a private stack.
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            player.draw(canvas);

            //draw smokepuffs
            for (Smoke sp : smoke) {
                sp.draw(canvas);
            }

            //draw missiles
            for (Missile m : missiles) {
                m.draw(canvas);
            }

            //draw explosion
            if(gameStarted) {
                explosion.draw(canvas);
            }

            //Efficient way to pop any calls to save() that happened after the save count reached saveCount.
            canvas.restoreToCount(savedState);
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    public void newGame() {
        // If the new score is better that the record, update and notify the hosting activity
        if(player.getScore()> currentHighScore) {
            currentHighScore = player.getScore();
            if (mHighScoreListener!=null)
                mHighScoreListener.onHighScoreUpdated(currentHighScore);
        }

        player.resetScore();

        disappear = false;

        missiles.clear();
        smoke.clear();

        player.resetDY();
        player.setTop(HEIGHT/2);


        newGameCreated = true;
    }
}
