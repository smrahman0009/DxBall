package com.example.musfiq.dxball;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class GameCanvas extends Activity {

    DxBall gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new DxBall(this);
        setContentView(gameView);

    }

    class DxBall extends SurfaceView implements Runnable{

        private boolean GAME_LEVEL_ONE;
        private boolean GAME_LEVEL_TWO;
        private boolean GAME_LEVEL_THREE;
    //    boolean firstTime;
        Thread gameThread = null;

        SurfaceHolder gameHolder;

        boolean playing;

        boolean isRunning = true;

        Canvas canvas;
        Paint paint;

        int  ballSpeed;
        int barSpeed;



        int xResulation;
        int yResulation;

        Bar bar;
        Ball ball;
        Brick[] bricks = new Brick[100];
        int numBricks = 0;

        // For sound FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        // The score
        int score = 0;

        // Lives
      //  int lives = 3;



        int gameLevel;


        int lives = 3;

        int noOfRows;
        int noOfColms;
        float targetScore;
        int levelTracker;



        public DxBall(Context context){
            super(context);
            levelTracker=0;


            //init game level
            GAME_LEVEL_ONE=true;
            GAME_LEVEL_TWO = false;
            GAME_LEVEL_THREE=false;

            gameHolder =getHolder();
            paint=new Paint();

            //Get Screen Resulation or Size
            Display display = getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            xResulation = size.x;
            yResulation = size.y;

            int barMovementSpeed=0;
            bar = new Bar(xResulation, yResulation,barMovementSpeed);


            //set Ball property
            //I consider ball width is as ball radius
            ball = new Ball();
            ball.setBallWidth(15);
            ball.reset(xResulation, yResulation);

            this.gameLevel=1;
            this.ballSpeed=40;
            this.barSpeed=17;

            //init no of rows in wall
            this.noOfRows =2;
            this.noOfColms =8;
            //init target score
            this.targetScore = this.noOfColms*this.noOfRows*10;

         //   firstTime = true;

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            makeBrickWall();

        }
        public void makeBrickWall(){


          //  score = 0;
            lives = 3;

            int brickWidth = xResulation / 8;
            int brickHeight = yResulation /12;


            numBricks = 0;

            int type=0;
            int collisionCounter=0;

            //set Bricks positions
            for(int column = 0; column < this.noOfColms; column ++ ){
                for(int row = 0; row < this.noOfRows; row ++ ){
                    if(column%2==0 && row%2==0){
                        type=0;
                        bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight,type,0);
                    }
                    else{

                        type=1;
                        bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight,type,1);
                    }

                    numBricks ++;

                }
            }

        }


        @Override
        public void run() {
            while (playing){
                setInitPosition();
                runGame();
            }
        }

        public  void setInitPosition(){

            if (gameHolder.getSurface().isValid()){
                canvas = gameHolder.lockCanvas();

                //DRAW BACKGROUND

                canvas.drawColor(Color.parseColor("#4CAF50"));




                // draw bar
                paint.setColor(Color.parseColor("#0272A6"));
                canvas.drawRect(bar.getBar(), paint);



                paint.setColor(Color.argb(255, 0, 0, 50));
                canvas.drawOval(ball.getBall(),paint);



                //Draw Wall
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {

                        if (bricks[i].getType()==1) {
                            paint.setColor(Color.parseColor("#FFEB3B"));
                            canvas.drawRect(bricks[i].getBrick(), paint);
                        }
                        else if (bricks[i].getType()==0){
                            paint.setColor(Color.parseColor("#FFC107"));
                            canvas.drawRect(bricks[i].getBrick(), paint);

                        }

                    }
                }

                paint.setColor(Color.argb(255,  0, 0, 2));

                // Draw  score
                paint.setTextSize(40);
                canvas.drawText("Point: " + score , xResulation-200,50, paint);
                canvas.drawText( " gameLevel: " + gameLevel, xResulation-600,50, paint);
                canvas.drawText( " Lives: " + lives, xResulation-1000,50, paint);
                canvas.drawText( " h speed: " + ball.getHoraizontalSpeed(), xResulation-1000,200, paint);

                gameHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void runGame(){

            if(!isRunning){


                ball.update(ballSpeed);
                bar.update(barSpeed);

                //Balls collision with bricks
                for(int i = 0; i < numBricks; i++){

                    if (bricks[i].getVisibility()){

                        if(RectF.intersects(bricks[i].getBrick(),ball.getBall())) {
                            if (bricks[i].getCollisionCounter()==0){
                                bricks[i].setInvisible();
                                score = score + 10;
                                levelTracker++;

                            }
                            else if (bricks[i].getCollisionCounter()==1){
                                bricks[i].setType(0);
                                bricks[i].setCollisionCounter();
                            }
                            //make sound when ball collid with the bricks
                            soundPool.play(explodeID, 1, 1, 0, 0, 1);

                            ball.setVerticalSpeed();

                        }
                    }
                }
                // Check for ball colliding with the bar and make sound
                if(RectF.intersects(bar.getBar(),ball.getBall())) {
                    ball.setRandomXVelocity();
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(bar.getBar().top - 2);
                    soundPool.play(beep1ID, 1, 1, 0, 0, 1);
                }

                // Bounce the ball back when it hits the bottom of screen
                // And deduct a life
                if(ball.getBall().bottom > yResulation){
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(yResulation - 2);

                    // Lose a life
                    lives --;
                    soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                    //when total numBricks=0 then game finish
                    if(lives == 0){
                        isRunning = true;
                        finish();
                        makeBrickWall();
                    }


                }

                if(ball.getBall().top < 0){
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(100);
                    soundPool.play(beep2ID, 1, 1, 0, 0, 1);
                }

                // If the ball hits left wall bounce
                if(ball.getBall().left < 0){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(20);
                    soundPool.play(beep3ID, 1, 1, 0, 0, 1);
                }

                // If the ball hits right wall bounce
                if(ball.getBall().right > xResulation){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(xResulation - 50);
                    soundPool.play(beep3ID, 1, 1, 0, 0, 1);
                }

                //check game score and  go to the next level
                if (score == targetScore && GAME_LEVEL_ONE==true){
                    GAME_LEVEL_ONE=false;
                    GAME_LEVEL_TWO=true;
                    ballSpeed= ballSpeed-5;
                    gameLevel=2;
                    this.noOfRows =3;
                    targetScore = targetScore+(this.noOfRows*this.noOfColms)*10;
                    ball.reset(xResulation,yResulation);
                    bar.barPositionReset();
                    makeBrickWall();
                    // levelTracker=0;
                }
                else if (score == targetScore && GAME_LEVEL_TWO==true){
                    GAME_LEVEL_TWO=false;
                    GAME_LEVEL_THREE = true;
                    gameLevel=3;
                    ballSpeed = ballSpeed-5;
                    this.noOfRows =4;
                    targetScore = targetScore+(this.noOfRows*this.noOfColms)*10;
                    ball.reset(xResulation,yResulation);
                    bar.barPositionReset();
                    makeBrickWall();

                }
                else if (score == targetScore && GAME_LEVEL_THREE==true){
                  //  finish();
                    GAME_LEVEL_ONE = true;
                    GAME_LEVEL_TWO=false;
                    GAME_LEVEL_THREE = false;
                    gameLevel=1;
                    score=0;
                    ballSpeed = ballSpeed+10;
                    this.noOfRows =2;
                    targetScore = (this.noOfRows*this.noOfColms)*10;
                    ball.reset(xResulation,yResulation);
                    bar.barPositionReset();
                    makeBrickWall();

                }
                // }


            }

        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {

            }

        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    isRunning =false;
                    if (motionEvent.getX()> xResulation /2){
                        bar.setMovementState(bar.RIGHT);
                    }
                    else {
                        bar.setMovementState(bar.LEFT);
                    }

                    break;
                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    bar.setMovementState(bar.STOPPED);
                    break;
            }
            return true;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        gameView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();

        gameView.pause();
    }
}