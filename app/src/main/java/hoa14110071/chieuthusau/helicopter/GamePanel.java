package hoa14110071.chieuthusau.helicopter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

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

    public GamePanel(Context context) {
        super(context);
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

        //create smoke
        smoke = new ArrayList<Smoke>();
        smokeStartTime=  System.nanoTime();


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
        while (retry && counter < 10000) {
            counter++;
            System.out.println(counter);
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.isPlaying()) {
                player.setPlaying(true);
            } else {
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
            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(elapsed > 120){
                smoke.add(new Smoke(player.getLeft(), player.getTop()+10));
                smokeStartTime = System.nanoTime();
            }


            for(int i = 0; i<smoke.size();i++) {
                smoke.get(i).update();
                if(smoke.get(i).getLeft()<-10) {
                    smoke.remove(i);
                }
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
            for(Smoke sp: smoke) {
                sp.draw(canvas);
            }


            //Efficient way to pop any calls to save() that happened after the save count reached saveCount.
            canvas.restoreToCount(savedState);
        }
    }
}
