package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;
import android.graphics.Canvas;



public class Player extends GameObject {


    //    private Bitmap helicopterImage;
    private int score;
    private boolean up;


    private boolean playing;
    private Animation animation;
    private long startTime;


    public Player(Bitmap helicopterImage, int w, int h, int numFrames) {
        super(100, GamePanel.HEIGHT / 2, w, h);

//        left = 100;
//        top = GamePanel.HEIGHT / 2;
//        this.helicopterImage = helicopterImage;
//        width = w;
//        height = h;

        dy = 0;

        this.score = 0;

        Bitmap[] image = new Bitmap[numFrames];
        for (int i = 0; i < numFrames; i++) {
            image[i] = Bitmap.createBitmap(helicopterImage, i * width, 0, width, height);
        }
        animation = new Animation(image, GamePanel.DELAY_ANIMATION_PLAYER_MS);
        startTime = System.nanoTime();
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();
        if (up) {
            dy -= 1;

        } else {
            dy += 1;
        }

        if (dy > 14) dy = 14;
        if (dy < -14) dy = -14;

        top += dy;

        if (top < 0) {
            top = 0;
        }else  if(top > 450)
            top = 450;
//        dy = 0;
    }

    public void draw(Canvas canvas) {
//        System.out.println(top);
        canvas.drawBitmap(animation.getImage(), left, top, null);
    }

    public void resetDY() {
        dy = 0;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getScore() {
        return score;
    }
    public void resetScore(){score = 0;}
}
