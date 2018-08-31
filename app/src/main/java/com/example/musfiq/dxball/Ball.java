package com.example.musfiq.dxball;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private float horaizontalSpeed;
    private float verticalSpeed;
   private float ballWidth ;
    private float ballHeight;
    RectF ball;

    public Ball(float ballHeight,float ballWidth){


        this.ballHeight=30;
        this.ballWidth=30;
        horaizontalSpeed = 300;
        verticalSpeed = -400;

        ball = new RectF();

    }

    public void setBallWidth(float width){
        this.ballWidth=width;
    }

    public float getBallWidth(){
        return this.ballWidth;
    }
    public void setBallHeight(float height){
       this.ballHeight=height;
    }

    public float getBallHeight(){
        return this.ballHeight;
    }
    public RectF getBall(){
        return ball;
    }

    public void update(long ballPosition){
        ball.left = ball.left + (horaizontalSpeed / ballPosition);
        ball.top = ball.top + (verticalSpeed / ballPosition);
        ball.right = ball.left + ballWidth;
        ball.bottom = ball.top - ballHeight;
    }

    public void setVerticalSpeed(){
        verticalSpeed = -verticalSpeed;
    }

    public void reverseXVelocity(){
        horaizontalSpeed = -horaizontalSpeed;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void stopVtclOverlape(float y){
        ball.bottom = y;
        ball.top = y - ballHeight;
    }

    public void stopHOverlap(float x){
        ball.left = x;
        ball.right = x + ballWidth;
    }

    public void reset(int x, int y){
        ball.left = x / 2;
        ball.top = y - 30;
        ball.right = x / 2 + ballWidth;
        ball.bottom = y - 20 - ballHeight;
    }

}