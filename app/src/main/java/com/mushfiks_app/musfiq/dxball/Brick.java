package com.mushfiks_app.musfiq.dxball;
import android.graphics.RectF;

public class Brick {

    private int collisionCounter;
    private int type;
    private boolean isVisible;
    private RectF brick;

    public Brick(int row,int col,int width,int height,int type,int collisionCounter){
        this.collisionCounter=collisionCounter;
        this.type=type;
        isVisible = true    ;
        int padding = 2;


        brick = new RectF(col*width+padding,
                row*height+padding,
                col*width+width-padding,
                row*height+height-padding);
    }
    public void setCollisionCounter(){
        this.collisionCounter=0;
    }
    public  int getCollisionCounter(){
        return this.collisionCounter;
    }
    public RectF getBrick(){
        return this.brick;
    }


    public void setInvisible(){
        isVisible = false;
    }

    public void setType(int type){
        this.type =type;
    }

    public int getType(){
        return this.type;
    }


    public boolean getVisibility(){
        return this.isVisible;
    }
}