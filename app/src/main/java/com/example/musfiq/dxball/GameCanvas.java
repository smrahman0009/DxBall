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

        volatile boolean playing;

        boolean paused = true;

        Canvas canvas;
        Paint paint;

       int  ballSpeed;
       int barSpeed;



        int screenX;
        int screenY;

        Bar bar;
        Ball ball;
        Brick[] bricks = new Brick[200];
        int numBricks = 0;



        // The score
        int score = 0;

        // Lives
        int lives = 3;



        public DxBall(Context context){
            super(context);


            gameHolder =getHolder();
            paint=new Paint();

            Display display = getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            bar = new Bar(screenX,screenY);
            ball = new Ball(screenX,screenY);

            //init ball speed and bar speed
            this.ballSpeed=100;
            this.barSpeed=80;

            firstTime = true;

            createBricksAndRestart();

        }
        public void createBricksAndRestart(){

            // Put the ball back to the start
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            // Build a wall of bricks
            numBricks = 0;

            for(int column = 0; column < 8; column ++ ){
                for(int row = 0; row < 3; row ++ ){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks ++;
                }
            }
            // Reset scores and lives

        }


        @Override
        public void run() {
            while (playing){

               if(!paused){
                    update();
                }

                // draw game canvas
                draw();

            }
        }

        public void update(){
            bar.update(barSpeed);
            ball.update(ballSpeed);

            // Check for ball colliding with a brick
            for(int i = 0; i < numBricks; i++){

                if (bricks[i].getVisibility()){

                    if(RectF.intersects(bricks[i].getRect(),ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                    }
                }
            }
            // Check for ball colliding with paddle
            if(RectF.intersects(bar.getRect(),ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(bar.getRect().top - 100);
            }

            // Bounce the ball back when it hits the bottom of screen
            // And deduct a life
            if(ball.getRect().bottom > screenY){
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                // Lose a life
                lives --;

                if(lives == 0){
                    paused = true;
                    finish();
                    createBricksAndRestart();
                }

            }

            // Bounce the ball back when it hits the top of screen
            if(ball.getRect().top < 0){
                ball.reverseYVelocity();
                ball.clearObstacleY(100);
            }

            // If the ball hits left wall bounce
            if(ball.getRect().left < 0){
                ball.reverseXVelocity();
                ball.clearObstacleX(100);
            }

            // If the ball hits right wall bounce
            if(ball.getRect().right > screenX - 30){
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 100);
            }

            // Pause if cleared screen
            if(score == numBricks * 10){
                paused = true;
                createBricksAndRestart();
            }

        }

        public void draw(){
            if (gameHolder.getSurface().isValid()){
                canvas = gameHolder.lockCanvas();
                // Draw the background color

                canvas.drawColor(Color.argb(255, 0, 100, 0));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 100, 0, 0));

                // Draw the paddle
                canvas.drawRect(bar.getRect(), paint);

                // Draw the ball
                paint.setColor(Color.argb(255, 0, 0, 50));
                canvas.drawOval(ball.getRect(),paint);


                // Draw the bricks
                paint.setColor(Color.argb(255,  7, 8, 56 ));

                // Draw the bricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // Draw the HUD

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);

                // Has the player cleared the screen?
                if(score == numBricks * 10){
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10,screenY/2, paint);
                }

                // Has the player lost?
                if(lives <= 0){
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10,screenY/2, paint);
                }
                // Draw everything to the screen

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
                    paused=false;
                    if (motionEvent.getX()>screenX/2){
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