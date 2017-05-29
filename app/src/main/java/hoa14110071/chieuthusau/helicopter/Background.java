package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by minhh on 28-May-17.
 */

public class Background {
    private Bitmap image;
    private int x = 0, y = 0, dx;

    public Background(Bitmap image) {
        this.image = image;
        dx = GamePanel.MOVE_SPEED;
    }

    public void update() {
        x += dx;
//        System.out.println(x);
        if (x < -GamePanel.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if(x<0)
        {
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, null);
        }
    }
}
