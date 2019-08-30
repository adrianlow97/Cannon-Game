package com.example.lowa19.homework3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by lowa19 on 4/2/2017.
 */
public class Targets {

    Rect target;
    Paint targetPaint;
    int xTopLeft, yTopLeft, xBottomRight, yBottomRight;
    private boolean upOrDown; //true = up, false = down

    public Targets(int initXTopLeft, int initYTopLeft, int initXBottomRight, int initYBottomRight)
    {
        xTopLeft = initXTopLeft;
        yTopLeft = initYTopLeft;
        xBottomRight = initXBottomRight;
        yBottomRight = initYBottomRight;
        targetPaint = new Paint();
        targetPaint.setColor(Color.RED);
        targetPaint.setStyle(Paint.Style.FILL);
        upOrDown = true;
    }

    public void drawMe(Canvas canvas)
    {
        canvas.drawRect(xTopLeft, yTopLeft, xBottomRight, yBottomRight, targetPaint);
    }

    public int getxTopLeft()
    {
        return this.xTopLeft;
    }
    public int getyTopLeft()
    {
        return this.yTopLeft;
    }
    public int getxBottomRight()
    {
        return this.xBottomRight;
    }
    public int getyBottomRight()
    {
        return this.yBottomRight;
    }

    public void moveTargets()
    {
        if(this.upOrDown == true)
        {
            if(this.yTopLeft <= 0)
            {
                this.upOrDown = false;
            }
            else
            {
                yTopLeft = yTopLeft - 10;
                yBottomRight = yBottomRight - 10;
            }
        }
        else
        {
            if(this.yBottomRight >= 1250)
            {
                this.upOrDown = true;
            }
            else
            {
                yTopLeft = yTopLeft +10;
                yBottomRight = yBottomRight +10;
            }
        }
    }
}
