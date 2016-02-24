package com.example.anders.breakout.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.anders.breakout.game_engine.GameView;

/**
 * Created by Anders on 2015-09-24.
 */
public class Player {
    private int x;
    private int y;
    private GameView gameView;
    private Bitmap bmp;
    private int width;
    private int height;
    private Rect bound;
    public static int lives = 3;
    public static int score = 0;

    public Player(GameView view, Bitmap bmp) {
        this.gameView = view;
        this.bmp = bmp;
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
        x = (view.getWidth()/2)-width/2;
        y = view.getHeight()-height-50;
        bound = new Rect(x, y, x + width, y + height);
    }


    public void setX(float x){
        this.x = (int)Math.min(Math.max(x - bmp.getWidth() / 2, 0), gameView.getWidth() - bmp.getWidth());//Stop the player att the margins
    }


    public void update(){
        bound.set(x, y, x+width, y+height);
    }

    public void onDraw(Canvas canvas) {
        update();
        //super.onDraw(canvas);
        canvas.drawBitmap(bmp, x, y, null);

    }

    public boolean isColliding(float x2, float y2) {
        return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public Rect getBound(){
        return bound;
    }
}
