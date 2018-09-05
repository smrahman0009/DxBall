package com.example.musfiq.dxball;

import android.annotation.SuppressLint;
import android.graphics.RectF;

public class Bar {

    private  int barMovementSpeed;
    private int screenX;


    private long width;
    private long height;


    private float x;


    private float y;

    private RectF rect;



    public  int STOPPED = 0;
    public  int LEFT = 1;
    public  int RIGHT = 2;


    private int paddleMoving = STOPPED;


    @SuppressLint("LongLogTag")
    public Bar(int screenX, int screenY,int barMovementSpeed){
        this.barMovementSpeed = barMovementSpeed;
        this.screenX=screenX;

        width = 200;
        height = 60;


        x = screenX / 2;
        y = screenY -30;

       rect = new RectF(x, y, x + width, y + height);
    }

    public void setBarWidth(int width){
        this.width=width;
    }
    public long getBarWidth(){
        return  width;
    }
    public void setBarHeight(int height){
        this.height=height;
    }
    public long getBarHeight(){
        return  height;
    }

    public RectF getBar(){

        return rect;
    }


    public void setMovementState(int state){
        paddleMoving = state;
    }


    public void update(int  barSpeed){
        if(paddleMoving == LEFT && rect.left>0 ){
            x = x -  barSpeed;
            rect.left = x;
            rect.right = x + width;
        }
        else if(paddleMoving == RIGHT && rect.right<screenX){
            x = x + barSpeed;
            rect.left = x;
            rect.right = x + width;

        }
    }

}