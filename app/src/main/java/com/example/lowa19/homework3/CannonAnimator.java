package com.example.lowa19.homework3;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.util.ArrayList;


/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 * 
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version September 2012
 */
public class CannonAnimator implements Animator {
	//game variables
	private ArrayList<Targets> targets;
	private int numTargets = 3;
	private int cannonBallRadius = 30;
	private int cannonPower = 100;
	private double gravity = 2.0;
	private boolean gameStart = false;
	private int endGameCooldown;
	private int winner;
	private int cannonCoolDownTime = 8;
	private boolean startTextUp = false;
	private int winningScore = 30;
	//human player variables
	private Cannon playerOneCannon;
	private FireButtonRect myFireButton;
	private ArrayList<Cannonball> playerOneCannonballs;
	Point startDrag = new Point(0,0);
	Point endDrag = new Point(0,0);
	private int playerOneScore = 0;
	private int playerOneFireCooldown = 0;
	//computer player variables
	private ArrayList<Cannonball> playerTwoCannonballs;
	private Cannon playerTwoCannon;
	private int playerTwoScore = 0;
	private int playerTwoFireCooldown = 0;

	public CannonAnimator()
	{
		//human player
		playerOneCannon = new Cannon(cannonPower, 1);
		myFireButton = new FireButtonRect();
		playerOneCannonballs = new ArrayList<>();
		//computer player
		playerTwoCannon = new Cannon( cannonPower, 2);
		playerTwoCannonballs = new ArrayList<>();

		targets = new ArrayList<>();
		constructTargets();
	}//ctor

	public int interval() {
		return 30;
	}

	public int backgroundColor() { return Color.rgb(180, 200, 255); }

	/**
	 * Action to perform on clock tick
	 *
	 * @param g the graphics object on which to draw
	 */
	public void tick(Canvas g)
	{
		if(gameStart == false && endGameCooldown ==0)
		{
			startScreen(g);
		}
		else if(gameStart == false)
		{
			endScreen(winner, g);
			endGameCooldown --;
		}
		else {
			//draw all of the objects on the canvas
			drawAllObjects(g);
			//check if any of the targets are hit or cannonball hit ground
			checkIfHit(g);
			//update the cannonball and target positions
			updatePositions();
			//make new targets if there are none left
			if(targets.isEmpty())
			{
				numTargets++;
				constructTargets();
			}
			//computer player aims cannon and fires
			computerActions();

			if(playerOneFireCooldown != 0)
			{
				playerOneFireCooldown --;
			}
			//check to see if either player won
			checkIfWin();

		}
	}

	public boolean doPause() {
		return false;
	}

	public boolean doQuit() {
		return false;
	}

	/**
	 * used to change cannon angle by dragging finger on screen
	 * or fires cannon if the 'button' is touched
	 * @param event a MotionEvent describing the touch
     */
	public void onTouch(MotionEvent event)
	{
		if(gameStart == false && endGameCooldown == 0)
		{
			gameStart = true;
		}
		int xPos = (int)event.getX();
		int yPos = (int)event.getY();
		double angle;
		if(myFireButton.fireClick(xPos,yPos) && playerOneFireCooldown == 0)
		{
			shootCannon(playerOneCannon);
			playerOneFireCooldown = cannonCoolDownTime;
		}
		else if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startDrag.x = xPos;
			startDrag.y = yPos;
		}
		else if ( event.getAction() == MotionEvent.ACTION_UP)
		{
			endDrag.x = xPos;
			endDrag.y = yPos;
			angle = calculateAngle(startDrag, endDrag);
			playerOneCannon.shiftCannon(angle);
		}
	}//onTouch

	/**
	 * creates cannonballs starting at tip of cannon
	 */
	private void shootCannon(Cannon c)
	{
		 Cannonball myCannonBall = new Cannonball(cannonBallRadius, c.getCannonMuzzleX(),
				c.getCannonMuzzleY(), c.getPowerX(), c.getPowerY(), gravity);
		if(c.getPlayerID() == 1)
		{
			playerOneCannonballs.add(myCannonBall);
		}
		else
		{
			playerTwoCannonballs.add(myCannonBall);
		}
	}//shootCannon

	/**
	 * creates targets at random locations between the two cannons and adds to arraylist
	 * each target is a 80x80 square
	 */
	private void constructTargets()
	{
		int minPosX = playerOneCannon.getRotationAxisX() + playerOneCannon.getWidth();
		int maxPosX = playerTwoCannon.getRotationAxisX() - playerTwoCannon.getWidth();
		for(int i = 0; i < numTargets; i++)
		{
			int randomX = (int)((maxPosX - minPosX)*Math.random());
			int randomY = (int)(playerOneCannon.getGroundHeight()*Math.random());
			if(randomY == playerOneCannon.getGroundHeight())
			{
				randomY = randomY -100;
			}
			Targets temp = new Targets(minPosX + randomX, randomY,
					minPosX + 80 + randomX, 80 + randomY);
			targets.add(temp);
		}
	}//constructTargets

	/**
	 * goes through each cannonball in the array
	 * if the cannonball hits a target, they both get removed
	 * sends a HIT  message to the GUI
	 */
	private void checkIfHit(Canvas canvas)
	{
		ArrayList<Cannonball> removeBallsOne = new ArrayList<>();
		ArrayList<Cannonball> removeBallsTwo = new ArrayList<>();
		ArrayList<Targets> removeTargets = new ArrayList<>();
		for (Cannonball b: playerOneCannonballs)
		{
			if((b.getPredictedY()+b.getRadius()) >= playerOneCannon.getGroundHeight()) //check if goint to hit ground
			{
				b.rolling(playerOneCannon);
			}
			if ((b.getxCoor() + b.getRadius()) >= 2000) //check if goes off screen
			{
				removeBallsOne.add(b);
			}
			else {
				for (Targets t : targets) {
					if (b.hitTarget(t)) {
						Paint textPaint = new Paint();
						textPaint.setColor(Color.WHITE);
						textPaint.setTextSize(70);
						canvas.drawText("HIT!", t.xTopLeft, t.yTopLeft, textPaint);
						removeTargets.add(t);
						removeBallsOne.add(b);
						playerOneScore++;
					}
				}
			}
		}
		for (Cannonball b: playerTwoCannonballs)
		{
			if((b.getPredictedY()+b.getRadius()) >= playerTwoCannon.getGroundHeight()) //check if goint to hit ground
			{
				b.rolling(playerTwoCannon);
			}
			if (b.getxCoor() < 0 )//check if goes off screen
			{
				removeBallsTwo.add(b);
			}
			else {
				for (Targets t : targets) {
					if (b.hitTarget(t)) {
						Paint textPaint = new Paint();
						textPaint.setColor(Color.WHITE);
						textPaint.setTextSize(70);
						canvas.drawText("HIT!", t.xTopLeft, t.yTopLeft, textPaint);
						removeTargets.add(t);
						removeBallsTwo.add(b);
						playerTwoScore++;
					}
				}
			}
		}
		playerOneCannonballs.removeAll(removeBallsOne);
		playerTwoCannonballs.removeAll(removeBallsTwo);
		targets.removeAll(removeTargets);
		/*
		copy code above but with the playerTwoCannonballs
		 */
	}//checkIfHit

	/**
	 * Use dot product formula with distances between points
	 * and cannon point of rotation ( a.b = |a||b|cos(angle) )
	 * @param start
	 * @param end
     * @return angle in radians
     */
	private double calculateAngle(Point start, Point end)
	{
		double theta;
		double alphaX = start.x - playerOneCannon.getRotationAxisX();
		double alphaY = start.y - playerOneCannon.getRotationAxisY();
		double betaX = end.x - playerOneCannon.getRotationAxisX();
		double betaY = end.y - playerOneCannon.getRotationAxisY();
		double magnitudeAlpha = Math.sqrt( (alphaX*alphaX) + (alphaY*alphaY));
		double magnitudeBeta = Math.sqrt((betaX*betaX) + (betaY*betaY));
		theta = Math.acos(((alphaX*betaX)+(alphaY*betaY))/(magnitudeAlpha*magnitudeBeta));
		//theta = theta*(180/Math.PI); //convert radians to degrees
		if(start.y < end.y)//up to down motion
		{
			theta = -theta;
		}
		return theta;
	}//calculateAngle

	/**
	 * creates a "start screen" that gives directions
	 * @param canvas
     */
	private void startScreen(Canvas canvas)
	{
		int centerScreenX = canvas.getWidth()/2;
		int centerScreenY = canvas.getHeight()/2;
		Paint startPaint = new Paint();
		startPaint.setColor(Color.RED);
		startPaint.setTextSize(80);
		if(startTextUp)
		{
			canvas.drawText("TAP SCREEN TO BEGIN", centerScreenX - 380, centerScreenY +85, startPaint);
			startTextUp = false;
		}
		else
		{
			canvas.drawText("TAP SCREEN TO BEGIN", centerScreenX - 380, centerScreenY +90, startPaint);
			startTextUp = true;
		}
		startPaint.setColor(Color.WHITE);
		startPaint.setTextSize(60);
		canvas.drawText("drag finger up and down to aim cannon", centerScreenX - 480,
				centerScreenY - 100, startPaint);
		canvas.drawText("first player to hit 30 targets wins",
				centerScreenX - 420, centerScreenY - 40, startPaint);
		startPaint.setTextSize(200);
		startPaint.setShadowLayer(8, 0.1f, 0.1f, Color.BLACK);
		startPaint.setStrokeWidth(10);
		startPaint.setStyle(Paint.Style.STROKE);
		canvas.drawText("Cannon Game", centerScreenX - 610, centerScreenY - 200, startPaint);

	}

	/**
	 * Checks scores to see if someone won
	 * used to call the endGameScreen for 200 ticks
	 */
	private void checkIfWin()
	{
		if(playerOneScore >= winningScore && playerTwoScore < winningScore)
			{
				endGameCooldown = 200;
				gameStart = false;
				winner = 1;
				resetGame();
			}
			else if (playerTwoScore >= winningScore && playerOneScore < winningScore)
			{
				endGameCooldown = 200;
				gameStart = false;
				winner = 2;
				resetGame();
			}
	}

	/**
	 * Prints the winner on the screen when the game finishes
	 * @param winnerID used to decide text to display
	 * @param canvas Canvas object
     */
	private void endScreen(int winnerID, Canvas canvas)
	{
		int centerScreenX = canvas.getWidth()/2;
		int centerScreenY = canvas.getHeight()/2;
		Paint endPaint = new Paint();
		endPaint.setColor(Color.RED);
		endPaint.setTextSize(140);
		endPaint.setShadowLayer(8, 0.1f, 0.1f, Color.MAGENTA);
		endPaint.setStrokeWidth(10);
		endPaint.setStyle(Paint.Style.STROKE);
		endPaint.setUnderlineText(true);

		if(winnerID == 1)
		{
			canvas.drawText( "Game Over Player One Wins", centerScreenX - 880, centerScreenY, endPaint);
			endPaint.reset();
			endPaint.setColor(Color.BLUE);
			endPaint.setTextSize(60);
			canvas.drawText( "start screen will be reloaded",
					centerScreenX - 480, centerScreenY +100, endPaint);
		}
		else
		{
			canvas.drawText( "Game Over Player Two Wins", centerScreenX - 880, centerScreenY, endPaint);
			endPaint.reset();
			endPaint.setColor(Color.BLUE);
			endPaint.setTextSize(60);
			canvas.drawText( "start screen will be reloaded",
					centerScreenX - 480, centerScreenY +100, endPaint);
		}
	}

	/**
	 * draws all objects on the canvas
	 * @param g Canvas
     */
	private void drawAllObjects(Canvas g)
	{
		myFireButton.drawMe(g);
		playerOneCannon.drawMe(g);
		playerTwoCannon.drawMe(g);

		for (Cannonball b : playerOneCannonballs) {
			b.drawMe(g);
		}

		for (Cannonball b: playerTwoCannonballs)
		{
			b.drawMe(g);
		}

		for (Targets t : targets) {
			t.drawMe(g);
		}

		Paint scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextSize(50);
		g.drawText("Player 1:"+playerOneScore, 50, 250, scorePaint);
		g.drawText("Player 2:"+playerTwoScore, 1500, 250, scorePaint);
	}

	/**
	 * updates both player's cannonball positions
	 */
	private void updatePositions()
	{
		for (Cannonball b : playerOneCannonballs) {
			b.updatePosition();
		}
		for(Cannonball b : playerTwoCannonballs)
		{
			b.updatePosition();
		}
		for (Targets t : targets) {
			t.moveTargets();
		}
	}

	/**
	 * lets computer shoot cannon at a random angle
	 */
	private void computerActions()
	{
		boolean makeAction = artificialWaitTime();
		if (makeAction && playerTwoFireCooldown == 0)
		{
			double angleDirection = Math.random();
			double randomAngle = (Math.PI/2)*Math.random();
				if(angleDirection>.5)
				{
					randomAngle = -randomAngle;
				}
				playerTwoCannon.shiftCannon(randomAngle);
				shootCannon(playerTwoCannon);
				playerTwoFireCooldown = cannonCoolDownTime;
		}
		if(playerTwoFireCooldown > 0) {
			playerTwoFireCooldown--;
		}
	}

	/**
	 * after game finishes, used to reset the game
	 */
	private void resetGame()
	{
		playerOneScore = 0;
		playerTwoScore = 0;
		numTargets = 3;
		targets.removeAll(targets);
		playerOneCannonballs.removeAll(playerOneCannonballs);
		playerTwoCannonballs.removeAll(playerTwoCannonballs);
		playerOneCannon = new Cannon(cannonPower, 1);
		playerTwoCannon = new Cannon(cannonPower, 2);
	}

	/**
	 * uses random number to make decision
	 * @return whether or not computer takes action
     */
	private boolean artificialWaitTime()
	{
		double decision = Math.random();
		if(decision > .7)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}//class CannonAnimator
