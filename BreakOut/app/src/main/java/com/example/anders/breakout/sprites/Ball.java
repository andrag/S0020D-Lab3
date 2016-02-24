package com.example.anders.breakout.sprites;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.anders.breakout.game_engine.GameView;

import java.util.Random;

/**
 * Created by Anders on 2015-09-24.
 */


public class Ball{

    private int x;
    private int y;
    private int xSpeed;//Only for the ball and maybe player
    private int ySpeed;
    private GameView gameView;
    private Bitmap bmp;
    private int width;
    private int height;
    private Rect bound;
    public boolean exists;
    public boolean win;
    public boolean loose;

    public Ball(GameView view, Bitmap bmp) {
        this.gameView = view;
        this.bmp = bmp;
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();

        setSpeed();
        resetPosition();
        bound = new Rect(x, y, x + width, y + height);
        exists = true;
        win = false;
        loose = false;

    }

    public void update(){
        //Bounce the ball if it reaches left or right edge of the screen
        if(x > gameView.getWidth() - width - xSpeed || x + xSpeed < 0){
            xSpeed = -xSpeed;
        }
        x = x + xSpeed;//Update position
        if(y > gameView.getHeight() - height - ySpeed){
            System.out.println("Game Over!");

            Player.lives--;
            if(Player.lives == 0){
                gameView.saveScore();
                exists = false;
                loose = true;
                Player.lives--;
            }

            else if(Player.lives > 0){
                x = gameView.getWidth()/2;
                y = gameView.getHeight()/2;
                gameView.playSound(gameView.lifeLossSoundID);
            }


        }
        if(y + ySpeed < 0){
            System.out.println("You won!");
            gameView.saveScore();
            exists = false;
            win = true;
            gameView.playSound(gameView.winSoundID);
        }
        y = y + ySpeed;
        bound.set(x, y, x+width, y+height);

    }


    public void onDraw(Canvas canvas) {
        if(exists){
            update();
            canvas.drawBitmap(bmp, x, y, null);
        }



    }

    public void setXSpeed(int xSpeed){
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(int ySpeed){
        this.ySpeed = ySpeed;
    }


    public void reverseXSpeed(){
        xSpeed = -xSpeed;
    }

    public void reverseYSpeed(){
        ySpeed = -ySpeed;
    }


    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getYSpeed(){
        return ySpeed;
    }

    public int getXSpeed(){
        return xSpeed;
    }


    //Use center when bouncing off from blocks. Can calculate dx and dy and compare.
    public int getCenterX(){
        return x + width/2;
    }

    public int getCenterY(){
        return y + height/2;
    }




    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Rect getBound(){
        return bound;
    }

    public void resetPosition(){
        x = gameView.getWidth()/2;
        y = gameView.getHeight()/2;
    }

    public void setSpeed(){
        Random rnd = new Random();
        xSpeed = 5*(rnd.nextInt(10)-5);
        ySpeed = 5*(rnd.nextInt(10)+5);//+5 to avoid that y-speed becomes 0.

    }


}
