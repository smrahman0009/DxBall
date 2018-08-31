package com.example.musfiq.dxball;
import android.graphics.RectF;

public class Brick {

    private boolean isVisible;
    private RectF brick;

    public Brick(int row,int col,int width,int height){
        isVisible = true    ;
        int padding = 1;
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

    public boolean getVisibility(){
        return isVisible;
    }
}
