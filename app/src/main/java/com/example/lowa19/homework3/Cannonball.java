package com.example.lowa19.homework3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by lowa19 on 4/2/2017.
 */
public class Cannonball {

    private int radius, xCoor, yCoor;
    private double xVelocity, yVelocity, gravity;
    private Paint ballPaint;
    private int time; //time that cannonball was released

    public Cannonball(int initRadius, int initX, int initY, double initVelocityX, double initVelocityY, double initGravity)
    {
        ballPaint = new Paint();
        ballPaint.setColor(Color.BLACK);
        ballPaint.setStyle(Paint.Style.FILL);
        radius = initRadius;
        xCoor = initX;
        yCoor = initY;
        xVelocity = initVelocityX;
        yVelocity = initVelocityY;
        gravity = initGravity;
    }

    public void drawMe(Canvas canvas)
    {
        canvas.drawCircle(xCoor, yCoor, radius, ballPaint);
    }

    public int getRadius()
    {
        return this.radius;
    }
    public int getxCoor()
    {
        return this.xCoor;
    }
    public int getyCoor()
    {
        return this.yCoor;
    }

    /**
     *
     * @param t the targets displayed on the screen
     * @return return true if the cannonball overlaps any of the targets
     */
    public boolean hitTarget(Targets t)
    {
        if(xCoor+radius >= t.getxTopLeft() && yCoor+radius >= t.getyTopLeft()
                && xCoor-radius <= t.getxBottomRight() && yCoor-radius <=t.getyBottomRight())
        {
            return true;
        }
        return false;
    }

    /**
     * This method changes the position of a cannonball object for each tick
     * uses gravity and calculates the change in distance using physics equations
     */
    public void updatePosition()
    {
        //changes the x and y coordinates of the cannonball
        int changeX = (int)xVelocity; //x=vt+1/2at^2, a = 0
        int changeY = (int)(yVelocity + .5*gravity*time*time); //y=vt+1/2at^2, a = gravity
        this.xCoor = this.xCoor + changeX;
        this.yCoor = this.yCoor + changeY;
        this.time++;
    }

    /**
     * used to see if the cannonball is going to hit ground
     * @return expected y coordinate of cannonball
     */
    public int getPredictedY()
    {
        return this.yCoor + (int)(this.yVelocity + .5*this.gravity*this.time*this.time);
    }

    /**
     * when the cannonball hits the ground
     * there will no longer be change in y-direction
     */
    public void rolling(Cannon cannon)
    {
        this.yCoor = cannon.getGroundHeight() - this.radius;
        this.gravity =  0;
        this.yVelocity = 0;
    }
}
