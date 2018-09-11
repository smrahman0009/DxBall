
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

        int wallRows;
        int levelTracker;



        public DxBall(Context context){
            super(context);
            levelTracker=0;

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


            //Draw ball
            int ballHeight=30;
            int ballWidth=30;
            ball = new Ball(ballHeight,ballWidth);
            ball.reset(xResulation, yResulation);

            this.gameLevel=1;
            this.ballSpeed=40;
            this.barSpeed=17;

            this.wallRows=2;

            firstTime = true;

            makeBrickWall();

        }
        public void makeBrickWall(){


            int brickWidth = xResulation / 8;
            int brickHeight = yResulation /12;


            numBricks = 0;

            int type=0;
            int collisionCounter=0;
            //build wall

            for(int column = 0; column < 8; column ++ ){
                for(int row = 0; row < this.wallRows; row ++ ){
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


                canvas.drawColor(Color.argb(255, 0, 100, 0));


                paint.setColor(Color.argb(255, 100, 0, 0));

                // draw bar
                canvas.drawRect(bar.getBar(), paint);



                paint.setColor(Color.argb(255, 0, 0, 50));
                canvas.drawOval(ball.getBall(),paint);



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

                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw  score
                paint.setTextSize(40);
                canvas.drawText("Point: " + score , xResulation-200,50, paint);
                canvas.drawText( " Lives: " + lives, xResulation-200,200, paint);
                canvas.drawText( " gameLevel: " + gameLevel, xResulation-600,50, paint);
                //       canvas.drawText( " destroyed bricks : " + destroyedBricks, xResulation-600,200, paint);

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
                                //  numBricks--;
                                score = score + 10;
                                levelTracker++;

                            }
                            else if (bricks[i].getCollisionCounter()==1){
                                bricks[i].setCollisionCounter();
                            }

                            ball.setVerticalSpeed();

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

               /* if (score==numBricks*10&&gameLevel==3){
                    finish();
                }
                if (score==numBricks*10&&gameLevel==2){
                    gameLevel=3;
                }*/
                // Pause if cleared screen
                //numBricks*10
                if(score==numBricks*10 ){
                    //gameLevel
                    if (levelTracker==16){
                        this.wallRows=4;
                        gameLevel=2;
                        // ballSpeed=ballSpeed-15;
                        ball.reset(xResulation,yResulation);
                        bar.barPositionReset();
                        //  setInitPosition();
                        //isRunning = true;
                        // this.wallRows=2;
                        makeBrickWall();
                        // levelTracker=0;
                    }
                    if (levelTracker==48)finish();
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