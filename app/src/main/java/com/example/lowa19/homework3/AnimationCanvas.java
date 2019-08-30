package com.example.lowa19.homework3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * A SurfaceView which allows which an animation to be drawn on it by a
 * Animator.
 * 
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version September 2012
 * 
 * 
 */
public class AnimationCanvas extends SurfaceView implements OnTouchListener{

	// instance variables
	private Animator animator; // our aninimator
	private CannonThread cannonThread = null; // thread to generate ticks
	private Paint backgroundPaint = new Paint();

	/**
	 * To create an animation canvas, you must supply an instance of a class
	 * that implements the Animator interface. This object will be relied upon
	 * to actually draw each from of the animation.
	 * 
	 * @param context
	 *            - a reference to the activity this animation is run under
	 * @param anim
	 *            - the animator that will do the drawing
	 */
	public AnimationCanvas(Context context, Animator anim) {
		super(context);

		// Tell the OS that *yes* I will draw stuff
		setWillNotDraw(false);

		// initialize the animator instance variable from the parameter
		animator = anim;
		
		//Begin listening for touch events
		this.setOnTouchListener(this);

		// create and start a thread to generate "ticks" for the animator
		// with the frequency that it desires
		this.cannonThread = new CannonThread(getHolder());
		cannonThread.start();

		// Initialize the background color paint as instructed by the animator
		backgroundPaint.setColor(anim.backgroundColor());
	}// ctor

	/**
	 * Thread subclass to control the game loop
	 * 
	 * Code adapted from Android:How to Program by Deitel, et.al., first edition
	 * copyright (C)2013.
	 * 
	 */
	private class CannonThread extends Thread {

		// a reference to a SurfaveView's holder. This is used to "lock" the
		// canvas when we want to write to it
		private SurfaceHolder surfaceHolder;

		// controls animation stop/go based upon instructions from the Animator
		private boolean threadIsRunning = true;

		/** ctor inits instance variables */
		public CannonThread(SurfaceHolder holder) {
			surfaceHolder = holder;
			setName("CannonThread");
		}

		/**
		 * causes this thread to pause for a given interval.
		 * 
		 * @param interval
		 *            duration in milliseconds
		 */
		private void sleep(int interval) {
			try {
				Thread.sleep(interval); // use sleep to avoid busy wait
			} catch (InterruptedException ie) {
				// don't care if we're interrupted
			}
		}// sleep

		/**
		 * This is the main animation loop. It calls the Animator's draw()
		 * method at regular intervals to creation the animation.
		 */
		@Override
		public void run() {

			Canvas canvas = null;// ref to canvas animator draws upon
			long lastTickEnded = 0; // when the last tick ended

			while (threadIsRunning) {

				// stop if the animator asks for it
				if (animator.doQuit())
					break;

				// pause while the animator wishes it
				while (animator.doPause()) {
					sleep(animator.interval());
				}// while

				// Pause to honor the animator's tick frequency specification
				long currTime = System.currentTimeMillis();
				long remainingWait = animator.interval()
						- (currTime - lastTickEnded);
				if (remainingWait > 0) {
					sleep((int) remainingWait);
				}

				// Ok! We can draw now.
				try {
					// lock the surface for drawing
					canvas = surfaceHolder.lockCanvas(null);
					
					//paint the background
					if (canvas != null)
					{
						canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

						// tell the animator to draw the next frame
						synchronized (surfaceHolder) {
							animator.tick(canvas);
						}// synchronized
					}
				}// try
				finally {
					// release the canvas
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}

				// Note when this tick ended
				lastTickEnded = System.currentTimeMillis();

			}// while
		}// run
	}

	/** 
	 * if I am touched, pass the touch event to the animator
	 */
	public boolean onTouch(View v, MotionEvent event) {
		this.animator.onTouch(event);
		return true;
	};// class CannonThread

}// class AnimationCanvas
