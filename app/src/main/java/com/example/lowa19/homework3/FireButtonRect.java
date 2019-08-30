package com.example.lowa19.homework3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * This class creates a "button" that fires the cannon
 * Created by kaleolow on 4/4/17.
 */

public class FireButtonRect
{
    Rect fireButton;
    Paint buttonPaint, textPaint;

    public FireButtonRect()
    {
        fireButton = new Rect(50, 50, 200, 200);
        buttonPaint = new Paint();
        buttonPaint.setColor(Color.MAGENTA);
        buttonPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(40);
    }

    public void drawMe(Canvas canvas)
    {
        canvas.drawRect(fireButton, buttonPaint);
        canvas.drawText("FIRE!", 100, 125, textPaint);
    }

    /**
     *
     * @param x x-pos of touch
     * @param y y-pos of touch
     * @return true if touch was inside 'button'
     * 30 pixel leway
     */
    public boolean fireClick(int x, int y)
    {
        if(x>=20 && x<=230 && y>=20 && y<=230)
        {
            return true;
        }
        return false;
    }
}
