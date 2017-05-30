package hoa14110071.chieuthusau.helicopter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;




public class Smoke extends GameObject {
    private int r;

    public Smoke(int left, int top) {
        super(left, top, 0, 0);
        r = 5;
    }

    public void update() {
        left -= 10;
    }


    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(left - r, top - r, r, paint);
        canvas.drawCircle(left - r + 2, top - r - 2, r, paint);
        canvas.drawCircle(left - r + 4, top - r + 1, r, paint);
    }
}
