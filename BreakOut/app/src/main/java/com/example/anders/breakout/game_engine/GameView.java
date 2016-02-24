package com.example.anders.breakout.game_engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.example.anders.breakout.R;
import com.example.anders.breakout.sprites.Ball;
import com.example.anders.breakout.sprites.Block;

import com.example.anders.breakout.sprites.Player;
import com.example.anders.breakout.sprites.RetryButton;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;



/**
 * Created by Anders on 2015-09-24.
 */
public class GameView extends SurfaceView{
    //private Bitmap background;
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;

    //Sprites
    private Ball ball;
    private Player player;
    private RetryButton retryButton;
    private ArrayList<Block> blocks = new ArrayList<>();

    private long startTime;
    private static long endTime;
    private ArrayList<String> tempScore = new ArrayList<String>();

    //Images
    private Bitmap winBMP;
    private Bitmap restartBMP;
    private Bitmap gameOverBMP;
    private Bitmap blockImage;
    private Bitmap noLifeBMP;
    private Bitmap fullLifeBMP;
    private Bitmap twoLifeBMP;
    private Bitmap oneLifeBMP;
    private Bitmap startScreen;

    private SoundPool soundPool;
    public int bounceSoundID, hitSoundID, lifeLossSoundID, winSoundID;

    public boolean gameStarted = false;

    private int numberOfBlocks = 6;




    public GameView(Context context){
        super(context);
        gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                createSprites();//Create the sprites when we know that the surface is created, no sooner :)
                initiateSoundPool();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);//Exit the game loop
                while(retry){
                    try{
                        gameLoopThread.join();//Pause current thread until the gameLoop terminates.
                        retry = false;
                    }
                    catch(InterruptedException e){e.printStackTrace();}
                }
            }
        });

    }


    private void startGame(){
        startTime = System.currentTimeMillis();
        gameStarted = true;
    }

    private void createSprites(){
        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.mopedderliten);
        startScreen = startScreen.createScaledBitmap(startScreen, startScreen.getWidth()/2, startScreen.getHeight()/2, false);

        blockImage = BitmapFactory.decodeResource(getResources(), R.drawable.block);

        noLifeBMP = BitmapFactory.decodeResource(getResources(), R.drawable.nolife);
        noLifeBMP = noLifeBMP.createScaledBitmap(noLifeBMP, noLifeBMP.getWidth() / 5, noLifeBMP.getHeight() / 5, false);
        fullLifeBMP = BitmapFactory.decodeResource(getResources(), R.drawable.full_life);
        fullLifeBMP = fullLifeBMP.createScaledBitmap(fullLifeBMP, fullLifeBMP.getWidth() / 5, fullLifeBMP.getHeight() / 5, false);
        twoLifeBMP = BitmapFactory.decodeResource(getResources(), R.drawable.two_life);
        twoLifeBMP = twoLifeBMP.createScaledBitmap(twoLifeBMP, twoLifeBMP.getWidth() / 5, twoLifeBMP.getHeight() / 5, false);
        oneLifeBMP = BitmapFactory.decodeResource(getResources(), R.drawable.one_life);
        oneLifeBMP = oneLifeBMP.createScaledBitmap(oneLifeBMP, oneLifeBMP.getWidth() / 5, oneLifeBMP.getHeight() / 5, false);

        winBMP = BitmapFactory.decodeResource(getResources(), R.drawable.score);
        winBMP = winBMP.createScaledBitmap(winBMP, winBMP.getWidth() / 2, winBMP.getHeight() / 2, false);
        restartBMP = BitmapFactory.decodeResource(getResources(), R.drawable.retry);
        retryButton = new RetryButton(restartBMP, getWidth()/2 - restartBMP.getWidth()/2, getHeight()-restartBMP.getHeight()-50);

        gameOverBMP = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        Bitmap p = BitmapFactory.decodeResource(getResources(), R.drawable.playerface);

       for(int i = 0;i<6;i++){
            Block block = new Block(this, blockImage, (i+1)*(this.getWidth()/6)-(blockImage.getWidth()), 150);
            blocks.add(block);
        }

        ball = new Ball(this, b);
        player = new Player(this, p);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(gameStarted){
            canvas.drawColor(Color.BLACK);
            ball.onDraw(canvas);
            if(!ball.win && !ball.loose){
                player.onDraw(canvas);
            }

            if(player.getBound().intersect(ball.getBound())){
                Log.d("CV", "player och ball kolliderar!");
                bounceAgainstPlayer();
            }

            //Draw blocks
            if(!ball.win && !ball.loose){
                for(Block b : blocks){
                    if(b.exists()){
                        b.onDraw(canvas, null);
                    }
                    else{
                        if(b.opacity > 0){
                            b.fadeBlock();
                        }
                        Paint paint = new Paint();
                        paint.setAlpha(b.opacity);
                        b.onDraw(canvas, paint);
                    }
                }
            }

            checkBlockCollision();
            if(numberOfBlocks==0){
                resetBlocks();
            }


            if(!ball.exists){
                if(ball.win){
                    drawHighScore(canvas);
                }
                else if(ball.loose){
                    canvas.drawBitmap(gameOverBMP, getWidth()/2 - gameOverBMP.getWidth()/2, getHeight()/4 - gameOverBMP.getHeight()/2, null);
                }
                canvas.drawBitmap(retryButton.getImage(), retryButton.getBounds().left, retryButton.getBounds().top, null );
            }
            drawBars(canvas);
        }
        else{
            //Draw the start screen
            canvas.drawBitmap(startScreen, getWidth() / 2 - startScreen.getWidth() / 2, getHeight() / 2 - startScreen.getHeight() / 2, null);
        }

    }




    private void drawBars(Canvas canvas) {
        //Draw the life bar
        canvas.drawBitmap(noLifeBMP, getWidth() - noLifeBMP.getWidth(), 10, null);
        if(Player.lives > 0){
            if(Player.lives == 3){
                canvas.drawBitmap(fullLifeBMP, getWidth()- fullLifeBMP.getWidth(), 10, null);
            }
            else if(Player.lives == 2){
                canvas.drawBitmap(twoLifeBMP, getWidth()- twoLifeBMP.getWidth(), 10, null);
            }
            else if(Player.lives == 1){
                canvas.drawBitmap(oneLifeBMP, getWidth()- oneLifeBMP.getWidth(), 10, null);
            }
        }

        //Draw score text. Own method for this too?
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);
        String score = "Score:  "+Player.score;
        canvas.drawText(score, 10, 80, paint);
    }

    private void drawHighScore(Canvas canvas){
       /* The following should be a part of the highscore:
            Date    Game time   Lives left      Score
             */
        canvas.drawBitmap(winBMP, getWidth() / 2 - winBMP.getWidth() / 2, (getHeight() / 4) - (winBMP.getHeight() / 2), null);

        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);

        int height = getHeight()/3;

        canvas.drawText("Date",getWidth()/8-100 , height, paint);
        canvas.drawText("Your time", getWidth()/4+150, height, paint);
        canvas.drawText("Your lives", getWidth() / 2 + 100, height, paint);
        canvas.drawText("Your score", getWidth() - 250, height, paint);

        int tempHeight = height + 100;

        for(int i = 0; i<tempScore.size();i++){
            String[] strings = tempScore.get(i).split(",");
            String currentTime = strings[0];
            String playTime = strings[1];
            String lives = strings[2];
            String score = strings[3];
            canvas.drawText(currentTime, getWidth()/8-100,tempHeight, paint);
            canvas.drawText(playTime, getWidth()/4+230, tempHeight, paint);
            canvas.drawText(lives, getWidth()/2+180, tempHeight, paint);
            canvas.drawText(score, getWidth()-200, tempHeight, paint);
            tempHeight+=100;
        }
    }

    public void checkBlockCollision(){
        Iterator<Block> block = blocks.iterator();//For safe removal of blocks
        while(block.hasNext()){
            Block b = block.next();
            if(b.exists()){
                if(ball.getBound().intersect(b.getBound())){
                    b.destroy();
                    playSound(hitSoundID);
                    ball.reverseYSpeed();
                    Player.score++;
                    numberOfBlocks--;
                }
            }
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction() & MotionEvent.ACTION_MASK;//Bitwise AND with a mask gives an int naming the user action.

        switch(action){
            case MotionEvent.ACTION_DOWN:{
                //First finger is touching the screen. This is the primary finger -
                // See more at: http://www.survivingwithandroid.com/2012/08/multitouch-in-android.html#sthash.cUuCfAZD.dpuf
                Log.d("CV", "Pointer down");
                if(gameStarted){
                    if((ball.win || ball.loose)){
                        if(retryButton.isColliding(event.getX(), event.getY())){
                            System.out.println("Nu tryckte du mitt i retryknappen!");
                            restartGame();
                        }
                        else System.out.println("Det är win eller loose men registrerar inte koordinater med contain.");
                    }

                }
                else startGame();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                //Finger is moving on the screen, do stuff if we are within the player area
                Log.d("CV","Pointer Move ");
                System.out.println("Moooving");
                synchronized (getHolder()){//Avoid collisions among threads
                    if(player.isColliding(event.getX(), event.getY())){
                        player.setX(event.getX());
                    }


                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                //Another finger is going down. This is not the primary finger
                Log.d("CV", "Other point down");
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                //Another finger is going up. This is not the primary finger
                Log.d("CV", "Other point up");
                break;
            }
            case MotionEvent.ACTION_UP: {
                //The primary finger is going up
                Log.d("CV", "Pointer up");
                break;
            }
        }
        return true;
    }




    private void bounceAgainstPlayer(){
        Log.d("CV", "Inne i bounce ball");
        //We have a collision, check if the ball is coming from above or the sides
        if(ball.getY() < player.getY() || ((ball.getX()>=player.getX() && (ball.getX()+ball.getWidth()) < (player.getX()+player.getWidth())))){

            //Check if the ball is hitting the player in the middle.
            if((ball.getX() >= (player.getX() + (2* (player.getWidth()/5))) &&
                    (ball.getX() < (player.getX() + (3*(player.getWidth()/5)))))){

                ball.setXSpeed(0);
                if(ball.getYSpeed()>0){
                    ball.reverseYSpeed();
                }
            }

            //The middle left side of the player
            else if((ball.getX() >= (player.getX() + (player.getWidth()/5)) && (ball.getX() < (player.getX() + (2*player.getWidth()/5))))){
                    //Reverse y and set x negative
                ball.setXSpeed(-Math.abs(ball.getXSpeed())-5);
                if(ball.getYSpeed() > 0){
                    ball.reverseYSpeed();
                }
            }

            //The far left side of player
            else if((ball.getCenterX() >= player.getX() && (ball.getX() < (player.getX() + player.getWidth()/5)))){
                //Reverse y and set x negative
                ball.setXSpeed(-Math.abs(ball.getXSpeed())-20);
                if(ball.getYSpeed() > 0){
                    ball.reverseYSpeed();
                }
            }

            //The middle right side
            else if((ball.getX() >= (player.getX() + (3* (player.getWidth()/5))) && (ball.getX() < (player.getX() + (4*(player.getWidth()/5)))))){
                //Reverse y and set x negative
                ball.setXSpeed(Math.abs(ball.getXSpeed())+5);
                if(ball.getYSpeed() > 0){
                    ball.reverseYSpeed();
                }
            }

            //The far right side
            else if((ball.getX() >= (player.getX() + (4* (player.getWidth()/5))) && (ball.getCenterX() < (player.getX() + (player.getWidth()))))){
                //Reverse y and set x negative
                ball.setXSpeed(Math.abs(ball.getXSpeed())+20);
                if(ball.getYSpeed() > 0){
                    ball.reverseYSpeed();
                }
            }
            else if((ball.getCenterX() < player.getX() && ball.getXSpeed()>0) || (ball.getCenterX() > (player.getX() + player.getWidth()) && ball.getXSpeed()<0)){
                ball.reverseXSpeed();
            }
            playSound(bounceSoundID);

        }
        else {
            ball.reverseXSpeed();
            playSound(bounceSoundID);
        }
    }



    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date now = new Date();
        String dateString = sdf.format(now);
        return dateString;
    }

    public void saveScore(){
         /* The following columns should be in the highscore:
            Date    Game time   Lives left      Score
             */

        endTime = (System.currentTimeMillis() - startTime)/1000;
        String score = getCurrentTime()+","+endTime+","+Player.lives+","+Player.score;

        if(tempScore.size()==7) tempScore.clear();
        tempScore.add(score);
    }


    public void restartGame(){

        Player.lives = 3;
        Player.score = 0;

        ball.resetPosition();
        ball.setSpeed();
        ball.win = false;
        ball.loose = false;
        ball.exists = true;

        for(Block b : blocks){
            b.setExist();
            b.setOpaque();
        }

        numberOfBlocks = 6;

        startTime = System.currentTimeMillis();
    }

    public void resetBlocks(){
        for(Block b : blocks){
            b.setExist();
            b.setOpaque();
        }
        numberOfBlocks = 6;
    }

    public void initiateSoundPool(){

        //SoundPool is deprecated in level 21 and above, check compatibility.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)//Don't have to define. Default should be ok.
                    .setUsage(AudioAttributes.USAGE_GAME)//Don't have to define. Default should be ok.
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)//Maximum number of simultaneous sound streams.
                    .setAudioAttributes(aa)//Might as well have gone with the default and skip aa
                    .build();

            bounceSoundID = soundPool.load(getContext(), R.raw.bounce, 1);
            hitSoundID = soundPool.load(getContext(), R.raw.hit, 1);
            lifeLossSoundID = soundPool.load(getContext(), R.raw.lifeloss, 1);
            winSoundID = soundPool.load(getContext(), R.raw.win, 1);


        }

        else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 1);
            bounceSoundID = soundPool.load(getContext(), R.raw.bounce, 1);
            hitSoundID = soundPool.load(getContext(), R.raw.hit, 1);
            lifeLossSoundID = soundPool.load(getContext(), R.raw.lifeloss, 1);
            winSoundID = soundPool.load(getContext(), R.raw.win, 1);

        }


    }

    public void playSound(int soundID){
        soundPool.play(soundID, 1, 1, 1, 0, 1);
    }
}










































