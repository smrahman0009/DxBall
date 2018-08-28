package com.example.musfiq.dxball;
import android.graphics.RectF;

public class Brick {
    private RectF rect;
    private boolean isVisible;

    public Brick(int row,int col,int width,int height){
        isVisible = true    ;
        int padding = 1;
        rect = new RectF(col*width+padding,
                row*height+padding,
                col*width+width-padding,
                row*height+height-padding);
    }
    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
