package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;

/**
 * Created by minhh on 29-May-17.
 */

public class Animation {
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private int delay;
    private boolean animatedOnce=false;

    public Animation(Bitmap[] frames, int delay) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        this.delay = delay;
    }

    public void update() {
        long elapsedTimeMs = (System.nanoTime() - startTime) / 1000000;
        if (elapsedTimeMs > delay) {
            currentFrame++;
            startTime = System.nanoTime();
        }
        if (currentFrame == frames.length) {
            currentFrame = 0;
            animatedOnce = true;
        }
    }

    public Bitmap getImage() {
        return frames[currentFrame];
    }

    public boolean isAnimatedAlready(){return animatedOnce;}

}
