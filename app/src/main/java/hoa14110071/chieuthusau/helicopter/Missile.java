package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by minhh on 30-May-17.
 */

public class Missile extends GameObject {
    //    private int score;
    private int speed;
    //    private int streamId;
    private Random rand = new Random();
    private Animation animation;


    public Missile(Bitmap missileImage, int left, int top, int width, int height, int score, int numFrames) {
        super(left, top, width, height);
        //toc do ten lua dua vao diem dang co
//        speed = 10 + (int) (rand.nextDouble() * score / 20);
//        if (speed > 300)
//            speed = 300;
        speed = 10 + (int) (rand.nextDouble() * score / 30);
        if (speed > 300)
            speed = 300;

        Bitmap[] image = new Bitmap[numFrames];
        for (int i = 0; i < numFrames; i++) {
            image[i] = Bitmap.createBitmap(missileImage, 0, i * height, width, height);
        }
        animation = new Animation(image, 100 - speed);
    }


    public void update() {
        left -= speed;
        animation.update();
    }


    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), left, top, null);
        } catch (Exception e) {
        }
    }
}
