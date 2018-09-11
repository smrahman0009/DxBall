package com.example.musfiq.dxball;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private float horaizontalSpeed;
    private float verticalSpeed;
    private float ballWidth ;
    private float ballHeight;
    RectF ball;

    public Ball(){


      //  this.ballHeight=15;
      //  this.ballWidth=15;
        horaizontalSpeed = 300;
        verticalSpeed = -300;

        ball = new RectF();

    }

    public void setBallWidth(float width){
        this.ballWidth=width;
    }

    public float getBallWidth(){
        return this.ballWidth;
    }
  /*  public void setBallHeight(float height){
        this.ballHeight=height;
    }*/

 /*   public float getBallHeight(){
        return this.ballHeight;
    }
    */
    public RectF getBall(){
        return ball;
    }

    public void update(long ballPosition){
        ball.left = ball.left + (horaizontalSpeed / ballPosition);
        ball.top = ball.top + (verticalSpeed / ballPosition);
        ball.right = ball.left + ballWidth;
        ball.bottom = ball.top - ballWidth;
    }

    public void setVerticalSpeed(){
        verticalSpeed = -verticalSpeed;
    }

    public void reverseXVelocity(){
        horaizontalSpeed = -horaizontalSpeed;
    }
    public void reverse65XVelocity(){
        horaizontalSpeed = -(horaizontalSpeed+20);
    }
    public void reverse35XVelocity(){
        horaizontalSpeed = -(horaizontalSpeed-15);
    }
    public void reverse25XVelocity(){
        horaizontalSpeed = -(horaizontalSpeed+40);
    }
    public void reverse75XVelocity(){
        horaizontalSpeed = -(horaizontalSpeed-10);
    }
    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(5);

        if(answer == 0){
            reverseXVelocity();
        }
        else if(answer==1){
            reverse65XVelocity();
        }
        else if (answer==2){
            reverse35XVelocity();
        }
        else if (answer==3){
            reverse25XVelocity();
        }
        else reverse75XVelocity();

    }

    public void stopVtclOverlape(float y){
        ball.bottom = y;
        ball.top = y - ballWidth;
    }

    public void stopHOverlap(float x){
        ball.left = x;
        ball.right = x + ballWidth;
    }

    public void reset(int x, int y){
        ball.left = x / 2;
        ball.top = y - 30;
        ball.right = x / 2 + ballWidth;
        ball.bottom = y -30 - ballWidth;
    }

}