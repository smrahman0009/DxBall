package com.example.musfiq.dxball;
import android.graphics.RectF;

public class Brick {

    private int type;
    private boolean isVisible;
    private RectF brick;

    public Brick(int row,int col,int width,int height,int type){
        isVisible = true    ;
        int padding = 1;
        this.type=type;
        brick = new RectF(col*width+padding,
                row*height+padding,
                col*width+width-padding,
                row*height+height-padding);
    }
    public RectF getBrick(){
        return this.brick;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public int getType(){
        return this.type;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
