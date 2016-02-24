package com.example.anders.breakout.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.anders.breakout.game_engine.GameView;

/**
 * Created by Anders on 2015-09-24.
 */
public class Block {
    private GameView gameView;
    private Bitmap bmp;
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean exists = true;
    private Rect bound;
    public int opacity;


    public Block(GameView view, Bitmap bmp, int x, int y) {
        this.gameView = view;
        this.bmp = bmp;
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
        this.x = x;
        this.y = y;
        bound = new Rect(x, y, x+width, y+height);
        setOpaque();
    }

   public void update(){
        bound.set(x, y, x+width, y+height);
    }

    public void onDraw(Canvas canvas, Paint paint){
            canvas.drawBitmap(bmp, x, y, paint);

    }

    public void destroy(){
        exists = false;
    }

    public Rect getBound(){
        return bound;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setExist(){
        exists = true;
    }

    public boolean exists(){
        return exists;
    }

    public void setOpaque(){
        opacity = 255;
    }

    public void fadeBlock(){
        opacity = opacity - 51;
    }
}
