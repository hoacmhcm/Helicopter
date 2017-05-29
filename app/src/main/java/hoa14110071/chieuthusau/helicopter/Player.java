package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by minhh on 29-May-17.
 */

public class Player extends GameObject {
    private Bitmap helicopterImage;
    private int score;
    private double dya;
    private boolean up;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    private boolean playing;
    private Animation animation;
    private long startTime;



    public Player(Bitmap helicopterImage, int w, int h, int numFrames) {
        left = 100;
        top = GamePanel.HEIGHT / 2;
        this.helicopterImage = helicopterImage;
        width = w;
        height = h;

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
            dy = (int) (dya -= 1.1);

        } else {
            dy = (int) (dya += 1.1);
        }

        if (dy > 14) dy = 14;
        if (dy < -14) dy = -14;

        top += dy * 2;
        dy = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), left, top, null);
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
