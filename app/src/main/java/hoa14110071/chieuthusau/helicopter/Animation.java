package hoa14110071.chieuthusau.helicopter;

import android.graphics.Bitmap;



public class Animation {
    //Animation của một đối tượng game được tạo ra bằng cách vẽ lên các khung hình khác nhau sau một khoảng thời gian nhất định cho đối tượng đó.
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
        //cứ sau một khoảng thời gian delay thì sẽ thay khung hình mới cho một đối tượng game bất kỳ tạo thành Animation cho đối tượng game đó.Ví dụ: máy bay, tên lửa,v.v....
        if (elapsedTimeMs > delay) {
            currentFrame++;
            startTime = System.nanoTime();
        }
        //nếu đã đến khung hình cuối cùng. Reset trở lại khung hình đầu tiên.
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
