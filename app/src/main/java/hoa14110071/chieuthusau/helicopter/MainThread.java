package hoa14110071.chieuthusau.helicopter;

import android.graphics.Canvas;
import android.view.SurfaceHolder;



public class MainThread extends Thread {
    private static int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        super.run();
        long startTime;
        //thoi gian thuc te hoan thanh 1 frame
        long timeCompleteMs;
        //thoi gian phai cho de hoan thanh 1 frame
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        //delay time la tg duoc tinh toan tren ly thuyet de tinh toan va render 1 frame
        long DELAY_TIME = 1000 / FPS;
        while (running) {
            startTime = System.nanoTime();
            canvas = null;
            try {
                //Start editing the pixels in the surface.
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        //Finish editing pixels in the surface.
                        surfaceHolder.unlockCanvasAndPost(canvas);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeCompleteMs = (System.nanoTime() - startTime) / 1000000;
            waitTime = DELAY_TIME - timeCompleteMs;
            try {
//                System.out.println(waitTime);
                sleep(waitTime);
            } catch (Exception e) {
//                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
//                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean b) {
        running = b;
    }
}
