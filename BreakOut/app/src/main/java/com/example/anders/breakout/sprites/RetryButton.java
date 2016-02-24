package com.example.anders.breakout.sprites;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Anders on 2015-10-26.
 */
public class RetryButton {

    private Bitmap bitmap;
    private int x, y, width, height;
    private Rect bounds;

    public RetryButton(Bitmap bitmap, int x, int y){
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        bounds = new Rect(x, y, width, height);
    }


    public Rect getBounds(){
        return bounds;
    }

    public boolean isColliding(float x2, float y2){
        return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
    }

    public Bitmap getImage(){
        return bitmap;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }



}
