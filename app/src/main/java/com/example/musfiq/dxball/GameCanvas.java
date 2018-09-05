package com.example.musfiq.dxball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameCanvas extends Activity {

    DxBall gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new DxBall(this);
        setContentView(gameView);

    }

    class DxBall extends SurfaceView implements Runnable{
        boolean firstTime;
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



        int gameLevel;
        int score = 0;


        int lives = 3;



        public DxBall(Context context){
            super(context);


            gameHolder =getHolder();
            paint=new Paint();

            Display display = getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            xResulation = size.x;
            yResulation = size.y;

            int barMovementSpeed=0;
            bar = new Bar(xResulation, yResulation,barMovementSpeed);

            int ballHeight=30;
            int ballWidth=30;
            ball = new Ball(ballHeight,ballWidth);
            ball.reset(xResulation, yResulation);

            this.gameLevel=1;
            this.ballSpeed=70;
            this.barSpeed=17;

            firstTime = true;

            makeBrickWall();

        }
        public void makeBrickWall(){


            int brickWidth = xResulation / 8;
            int brickHeight = yResulation / 12;


            numBricks = 0;

            int type=0;
            int collisionCounter=0;
            if (gameLevel==1){
                for(int column = 0; column < 10; column ++ ){
                    for(int row = 0; row < 2; row ++ ){
                        if(column%2==0){
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
            else if (gameLevel>=2){
                for(int column = 0; column < 10; column ++ ){
                    for(int row = 0; row < 4; row ++ ){
                        if(column%2==0){
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

        }


        @Override
        public void run() {
            while (playing){
             runGame();
            }
        }

        public void runGame(){

            if (gameHolder.getSurface().isValid()){
                canvas = gameHolder.lockCanvas();


                canvas.drawColor(Color.argb(255, 0, 100, 0));


                paint.setColor(Color.argb(255, 100, 0, 0));

                // draw bar
                canvas.drawRect(bar.getBar(), paint);



                paint.setColor(Color.argb(255, 0, 0, 50));
                canvas.drawOval(ball.getBall(),paint);






                if (gameLevel==1){
                    for(int i = 0; i < numBricks; i++){
                        if(bricks[i].getVisibility()) {

                            if (bricks[i].getType()==1) {
                                paint.setColor(Color.argb(255,  0, 0, 150 ));
                                canvas.drawRect(bricks[i].getBrick(), paint);
                            }
                            else if (bricks[i].getType()==0){
                                paint.setColor(Color.argb(255,  150, 0, 0));
                                canvas.drawRect(bricks[i].getBrick(), paint);
                            }

                        }
                    }
                }
                else if (gameLevel>=2){
                    for(int i = 0; i < numBricks; i++){
                        if(bricks[i].getVisibility()) {

                            if (bricks[i].getType()==1) {
                                paint.setColor(Color.argb(255,  100, 0, 100 ));
                                canvas.drawRect(bricks[i].getBrick(), paint);
                            }
                            else if (bricks[i].getType()==0){
                                paint.setColor(Color.argb(255,  50, 50, 50));
                                canvas.drawRect(bricks[i].getBrick(), paint);
                            }

                        }
                    }
                }



                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw  score
                paint.setTextSize(40);
                canvas.drawText("Point: " + score , xResulation-200,50, paint);
                canvas.drawText( " Lives: " + lives, xResulation-200,200, paint);
                canvas.drawText( " gameLevel: " + gameLevel, xResulation-600,200, paint);

                gameHolder.unlockCanvasAndPost(canvas);
            }

            if(!isRunning){
                if (gameLevel==1){
                    ball.update(ballSpeed);
                }
                else if (gameLevel>=2){
                    ball.update(ballSpeed/gameLevel);
                }
                bar.update(barSpeed);

                //Balls collision with bricks
                for(int i = 0; i < numBricks; i++){

                    if (bricks[i].getVisibility()){

                        if(RectF.intersects(bricks[i].getBrick(),ball.getBall())) {
                            if (bricks[i].getCollisionCounter()==0)bricks[i].setInvisible();
                            else if (bricks[i].getCollisionCounter()==1){
                                bricks[i].setCollisionCounter();
                            }

                            ball.setVerticalSpeed();
                            score = score + 10;
                        }
                    }
                }

                if(RectF.intersects(bar.getBar(),ball.getBall())) {
                    ball.setRandomXVelocity();
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(bar.getBar().top - 2);
                }


                if(ball.getBall().bottom > yResulation){
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(yResulation - 2);

                    // Lose a life
                    lives --;
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
                }

                // If the ball hits left wall bounce
                if(ball.getBall().left < 0){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(20);
                }

                // If the ball hits right wall bounce
                if(ball.getBall().right > xResulation){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(xResulation - 50);
                }

                // Pause if cleared screen
                if(score == numBricks * 10){
                    gameLevel=2;
                   // isRunning = true;
                    makeBrickWall();
                }
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