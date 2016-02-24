package com.example.anders.breakout.game_engine;

import android.graphics.Canvas;

/**
 * Created by Anders on 2015-09-24.
 */
public class GameLoopThread extends Thread{
    static final long FPS = 10;
    private GameView view;
    private boolean running = false;

    public GameLoopThread(GameView view){
        this.view = view;
    }

    public void setRunning(boolean run){
        running = run;
    }

    @Override
    public void run() {
        //Variables for calculating thread sleep time, for even motion.
        long ticksPS = 1000/FPS;
        long startTime;
        long sleepTime;
        while(running){
            startTime = System.currentTimeMillis();
            Canvas canvas = null;
            try{
                canvas = view.getHolder().lockCanvas();//Lock to use exclusively by this thread
                synchronized (view.getHolder()){
                    view.onDraw(canvas);
                }
            }
            finally{//Is executed even if something goes wrong in the try block
                if(canvas != null){
                    view.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis()-startTime);
            try{
                if(sleepTime > 0){//Check if we need to stall the thread for even motion.
                    sleep(sleepTime);
                }
                else sleep(10);
            }
            catch(Exception e){e.printStackTrace();}
        }
    }
}
