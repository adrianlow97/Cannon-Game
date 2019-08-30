package com.example.lowa19.homework3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by lowa19 on 4/2/2017.
 */
public class Cannon
{
    private Paint cannonPaint, wheelPaint;
    private Point topLeft, topRight, bottomLeft, bottomRight, wheelOrigin;
    private Path cannonBody;
    private double cannonAngle; //in radians
    double maxAngle;
    double minAngle;
    private int estimatedScreenWidth = 1800;
    private int wheelRadius = 50;
    private int x = 100;
    private int y = 1200; //for smoother view set to 1200
    private int power, playerID;
    private int height = 100;
    private int width = 300;
    private double topLeftAngle, topRightAngle, bottomRightAngle, bottomLeftAngle;
    private double topRightDistance, topLeftDistance;

    public Cannon(int initPower, int initPlayerID)
    {
        playerID = initPlayerID;
        power = initPower;
        cannonPaint = new Paint();
        cannonPaint.setColor(Color.GRAY);
        cannonPaint.setStyle(Paint.Style.FILL);
        wheelPaint = new Paint();
        wheelPaint.setColor(Color.YELLOW);
        wheelPaint.setStyle(Paint.Style.FILL);
        if(playerID == 1)
        {
            initPlayerOneCannon();
        }
        else //playerTwoCannon
        {
            initPlayerTwoCannon();
        }

    }

    public void drawMe(Canvas canvas)
    {
        //draw cannon
        cannonBody = new Path();
        cannonBody.moveTo(bottomLeft.x, bottomLeft.y);
        cannonBody.lineTo(topLeft.x, topLeft.y);
        cannonBody.lineTo(topRight.x, topRight.y);
        cannonBody.lineTo(bottomRight.x, bottomRight.y);
        cannonBody.lineTo(bottomLeft.x, bottomLeft.y);
        cannonBody.close();
        canvas.drawPath(cannonBody, cannonPaint);
        canvas.drawCircle(wheelOrigin.x, wheelOrigin.y, wheelRadius, wheelPaint);
        //draw 'ground'
        Paint grass = new Paint();
        grass.setColor(Color.GREEN);
        grass.setStyle(Paint.Style.FILL);
        Rect ground = new Rect(0, y + wheelRadius, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(ground, grass);
    }

    /**
     * changes angle of cannon and modifies points
     * @param angle is the angle from drag
     */
    public void shiftCannon(double angle)
    {
        double resultingAngle = this.cannonAngle + angle;

        if(resultingAngle <= this.maxAngle && resultingAngle >= this.minAngle) //limitations
        {
            this.cannonAngle = resultingAngle; //change the saved value for angle
            if (this.playerID == 1)
            {
                this.topLeftAngle = this.topLeftAngle + angle;
                this.topRightAngle = this.topRightAngle + angle;
                this.bottomRightAngle = this.bottomRightAngle + angle;
                changePoints();
            }
            else //playerTwoCannon
            {
                this.topRightAngle = this.topRightAngle + angle;
                this.topLeftAngle = this.topLeftAngle + angle;
                this.bottomLeftAngle = this.bottomLeftAngle + angle;
                changePoints();
            }
        }
    }
    /**
     * Used to set initial position of the cannonball
     * @return using top and bottom right positions
     * this method returns the X position using the midpoint formula
     */
    public int getCannonMuzzleX()
    {
        int midX;
        if(this.playerID == 1)
        {
             midX = (this.topRight.x + this.bottomRight.x) / 2;
            return midX;
        }
        else //playerTwoCannon
        {
            midX = (this.topLeft.x + this.bottomLeft.x) / 2;
            return midX;
        }
    }
    /**
     * Used to set initial position of the cannonball
     * @return using top and bottom right positions
     * this method returns the X position using the midpoint formula
     */
    public int getCannonMuzzleY()
    {
        int midY;
        if(this.playerID == 1)
        {
            midY = (this.topRight.y + this.bottomRight.y) / 2;
            return midY;
        }
        else //playerTwoCannon
        {
            midY = (this.topLeft.y + this.bottomLeft.y) / 2;
            return midY;
        }
    }

    public int getRotationAxisX()
    {
        return this.wheelOrigin.x;
    }

    public int getRotationAxisY()
    {
        return this.wheelOrigin.y;
    }

    /**
     * Used to set initial velocity of the cannonball
     * @return using set force that the cannon exerts
     * this method returns the force in the X-direction
     */
    public double getPowerX()
    {
        double forceX;
        if(this.playerID == 1)
        {
            forceX = power * Math.cos(this.cannonAngle);
            return forceX;
        }
        else //for playerTwoCannon
        {
            forceX = power * Math.cos(this.cannonAngle);
            return forceX;
        }
    }

    /**
     * Used to set initial velocity of the cannonball
     * @return using set force that the cannon exerts
     * this method returns the force in the Y-direction
     */
    public double getPowerY()
    {
        double forceY = -(power*Math.sin(this.cannonAngle));
        return forceY;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getPlayerID()
    {
        return this.playerID;
    }

    public int getGroundHeight()
    {
        return (this.y + this.wheelRadius);
    }

    /**
     *  initialize the cannon's variables for the main player
     */
    public void initPlayerOneCannon()
    {
        double dHeight = height;
        double dWidth = width;
        topRightAngle = Math.atan(dHeight/dWidth);
        topRightDistance = Math.sqrt((height*height) + (width*width));
        topLeftAngle = (Math.PI / 2);
        bottomRightAngle = 0;
        topLeft = new Point(x, y - height);
        topRight = new Point(x + width, y - height);
        bottomLeft = new Point(x, y);
        bottomRight = new Point(x + width, y);
        wheelOrigin = new Point(x,y);
        minAngle = 0;
        maxAngle = Math.PI/2;
        cannonAngle = 0;
    }

    /**
     * initialize the cannon's variables for the computer
     */
    public void initPlayerTwoCannon()
    {
        double dHeight = height;
        double dWidth = width;
        topLeftAngle = Math.PI - Math.atan(dHeight/dWidth);
        topLeftDistance = Math.sqrt((height*height) + (width*width));
        topRightAngle = (Math.PI/2);
        bottomLeftAngle = Math.PI;
        topRight = new Point(estimatedScreenWidth - x, y - height);
        topLeft = new Point(estimatedScreenWidth - x - width, y - height);
        bottomLeft = new Point(estimatedScreenWidth - x - width, y);
        bottomRight = new Point(estimatedScreenWidth - x, y);
        wheelOrigin = new Point(estimatedScreenWidth - x, y);
        minAngle = Math.PI/2;
        maxAngle = Math.PI;
        cannonAngle = Math.PI;
    }

    /**
     * changes the points using parametric
     * coordinates of a circle around translated origin
     */
    public void changePoints()
    {
        if (this.playerID == 1)
        {
            this.topLeft.x = x + (int) (height * Math.cos(topLeftAngle));
            this.topLeft.y = y - (int) (height * Math.sin(topLeftAngle));
            this.topRight.x = x + (int) (topRightDistance * Math.cos(topRightAngle));
            this.topRight.y = y - (int) (topRightDistance * Math.sin(topRightAngle));
            this.bottomRight.x = x + (int) (width * Math.cos(bottomRightAngle));
            this.bottomRight.y = y - (int) (width * Math.sin(bottomRightAngle));
        }
        else //playerTwoCannon
        {
            this.topRight.x = this.wheelOrigin.x + (int)(height * Math.cos(topRightAngle));
            this.topRight.y = this.wheelOrigin.y - (int)(height * Math.sin(topRightAngle));
            this.topLeft.x = this.wheelOrigin.x + (int)(topLeftDistance * Math.cos(topLeftAngle));
            this.topLeft.y = this.wheelOrigin.y - (int)(topLeftDistance * Math.sin(topLeftAngle));
            this.bottomLeft.x = this.wheelOrigin.x + (int)(width * Math.cos(bottomLeftAngle));
            this.bottomLeft.y = this.wheelOrigin.y - (int)(width * Math.sin(bottomLeftAngle));
        }
    }//changePoints

}
