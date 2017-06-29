package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//Vụ nổ của game
public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animation;
    private Bitmap spritesheet;

    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;
        row = 0;
        for(int i = 0; i<image.length; i++) {
            if(i%5==0&&i>0)row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);
        }
        animation= new Animation(image,GamePanel.DELAY_ANIMATION_PLAYER_MS);
    }


    public void draw(Canvas canvas) {
        if(!animation.isAnimatedAlready()) {
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }
    }


    public void update() {
        if(!animation.isAnimatedAlready()) {
            animation.update();
        }
    }

}
