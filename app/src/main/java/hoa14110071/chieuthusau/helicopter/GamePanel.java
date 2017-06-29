package hoa14110071.chieuthusau.helicopter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


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
    private boolean gameStarted;
    private boolean collision = false;
    private int currentHighScore;
    private static final int SCORE_BOOSTER = 3;


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
        //de xu li su kien onTouch can phai setFocusable
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
        smoke = new ArrayList<>();
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


            //them khoi phia sau may bay
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

            //them ten lua
            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            //cu 2s la ra 1 loat ten lua
            if (missileElapsed > 1000) {
                int mX = WIDTH + 10;
                int mY;

                // them ten lua sau do reset time
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
                missileStartTime = System.nanoTime();
            }

            //lap lai cu qua moi ten lua kiem tra xem co bi dung may bay khong
            for (int i = 0; i < missiles.size(); i++) {
                //cap nhat animation va vi tri ten lua
                missiles.get(i).update();

                if (collision(missiles.get(i), player)) {
                    missiles.remove(i);
                    player.setPlaying(false);
                    collision = true;
                    break;
                }

                //neu ten lua qua khoi ben trai man hinh thi remove no di
                if (missiles.get(i).getLeft() < -100) {
                    missiles.remove(i);
                }
            }
        } else {
            player.resetDY();
            if (!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getLeft(),
                        player.getTop() - 30, 100, 100, 25);

                if (collision) {
                    collision = false;
                }
            }

            explosion.update();
            long resetElapsed = (System.nanoTime() - startReset) / 1000000;

            if (resetElapsed > 2500 && !newGameCreated) {
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
            if (gameStarted) {
                explosion.draw(canvas);
            }

            drawText(canvas);

            //Efficient way to pop any calls to save() that happened after the save count reached saveCount.
            canvas.restoreToCount(savedState);
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }

    public void newGame() {
        // If the new score is better that the record, update and notify the hosting activity
        if (player.getScore() > currentHighScore) {
            currentHighScore = player.getScore();
            if (mHighScoreListener != null)
                mHighScoreListener.onHighScoreUpdated(currentHighScore);
        }

        player.resetScore();


        missiles.clear();
        smoke.clear();

        level = 1;

        player.resetDY();
        player.setTop(HEIGHT / 2);


        newGameCreated = true;
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("POINT: " + player.getScore() * SCORE_BOOSTER, 10, HEIGHT - 10, paint);
        canvas.drawText("HIGH SCORE: " + currentHighScore * SCORE_BOOSTER, WIDTH - 260, HEIGHT - 10, paint);

        if (!player.isPlaying() && newGameCreated && reset) {
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("NHẤP VÀO ĐỂ BẮT ĐẦU", WIDTH / 2 - 50, HEIGHT / 2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("NHẤP HOẶC GIỮ ĐỂ BAY LÊN", WIDTH / 2 - 50, HEIGHT / 2 + 20, paint1);
            canvas.drawText("THẢ RA ĐỂ RƠI XUỐNG", WIDTH / 2 - 50, HEIGHT / 2 + 40, paint1);
        }
    }
}
