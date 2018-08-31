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
        Brick[] bricks = new Brick[200];
        int numBricks = 0;




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

            bar = new Bar(xResulation, yResulation);

            ball = new Ball();

            this.ballSpeed=100;
            this.barSpeed=80;

            firstTime = true;

            createBricksAndRestart();

        }
        public void createBricksAndRestart(){


            ball.reset(xResulation, yResulation);

            int brickWidth = xResulation / 10;
            int brickHeight = yResulation / 10;


            numBricks = 0;

            for(int column = 0; column < 8; column ++ ){
                for(int row = 0; row < 3; row ++ ){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks ++;
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

            if(!isRunning){
                bar.update(barSpeed);
                ball.update(ballSpeed);


                for(int i = 0; i < numBricks; i++){

                    if (bricks[i].getVisibility()){

                        if(RectF.intersects(bricks[i].getBrick(),ball.getBall())) {
                            bricks[i].setInvisible();
                            ball.setVerticalSpeed();
                            score = score + 10;
                        }
                    }
                }
                // Check for ball colliding with bar
                if(RectF.intersects(bar.getBar(),ball.getBall())) {
                    ball.setRandomXVelocity();
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(bar.getBar().top - 100);
                }

                // Bounce the ball back when it hits the bottom of screen
                // And deduct a life
                if(ball.getBall().bottom > yResulation){
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(yResulation - 2);

                    // Lose a life
                    lives --;

                    if(lives == 0){
                        isRunning = true;
                        finish();
                        createBricksAndRestart();
                    }

                }

                // Bounce the ball back when it hits the top of screen
                if(ball.getBall().top < 0){
                    ball.setVerticalSpeed();
                    ball.stopVtclOverlape(100);
                }

                // If the ball hits left wall bounce
                if(ball.getBall().left < 0){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(100);
                }

                // If the ball hits right wall bounce
                if(ball.getBall().right > xResulation - 30){
                    ball.reverseXVelocity();
                    ball.stopHOverlap(xResulation - 100);
                }

                // Pause if cleared screen
                if(score == numBricks * 10){
                    isRunning = true;
                    createBricksAndRestart();
                }
            }
            if (gameHolder.getSurface().isValid()){
                canvas = gameHolder.lockCanvas();
                // Draw the background color

                canvas.drawColor(Color.argb(255, 0, 100, 0));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 100, 0, 0));

                // Draw the paddle
                canvas.drawRect(bar.getBar(), paint);


                // Draw the ball
                paint.setColor(Color.argb(255, 0, 0, 50));
                canvas.drawOval(ball.getBall(),paint);


                // Draw the bricks
                paint.setColor(Color.argb(255,  7, 8, 56 ));

                // Draw the bricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getBrick(), paint);
                    }
                }

                // Draw the HUD

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);

                gameHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }
        // If SimpleGameEngine Activity is started theb
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
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

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }
    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }
}