package uk.ac.reading.ps026478.playgroundwars;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * GameThread class extends the Thread class as this is where all the actions for the non UI
 * thread are handled along with the TheGame class which inherits from this one.
 *
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 19/4/17
 */
public abstract class GameThread extends Thread {
	//Different mMode states
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

	//Control variable for the mode of the game (e.g. STATE_WIN)
	protected int mMode = 1;

	//Control of the actual running inside run()
	private boolean mRun = false;
		
	//The surface this thread (and only this thread) writes upon
	private SurfaceHolder mSurfaceHolder;
	
	//the message handler to the View/Activity thread
	private Handler mHandler;
	
	//Android Context - this stores almost all we need to know
	private Context mContext;
	
	//The view
	public GameView mGameView;

	//We might want to extend this call - therefore protected
	protected int mCanvasWidth = 1;
	protected int mCanvasHeight = 1;

	//Last time we updated the game physics
	protected long mLastTime = 0;
 
	protected Bitmap mBackgroundImage;
	
	protected long score = 0;

    //Used for time keeping
	private long now;
	private float elapsed;

    //Used to ensure appropriate threading
    static final Integer monitor = 1;


	/**
	 * GameThread contructor takes the gameView param and creates a local reference so it can
	 * be changed from within this class.
	 *
	 * <p>It also sets the surface holder, handler and context of the thread based off the respective
	 * attributes from the GameView. This is also where the background image is set for the GameView</p>
	 * @param gameView Reference of the GameView that is run within the GameThread
	 */
	public GameThread(GameView gameView) {		
		mGameView = gameView;
		
		mSurfaceHolder = gameView.getHolder();
		mHandler = gameView.getmHandler();
		mContext = gameView.getContext();
		
		mBackgroundImage = BitmapFactory.decodeResource
							(gameView.getContext().getResources(), 
							R.drawable.background);
	}
	
	/*
	 * Called when app is destroyed, so not really that important here
	 * But if (later) the game involves more thread, we might need to stop a thread, and then we would need this
	 * Dare I say memory leak...
	 */

	/**
	 * cleanup function is called when the app is destroyed, more useful if multiple threads to nullify
	 * memory leak issues.
	 */
	public void cleanup() {		
		this.mContext = null;
		this.mGameView = null;
		this.mHandler = null;
		this.mSurfaceHolder = null;
	}

	/**
	 * setupBeginning is defined as abstract is it must be properly implemented and defined in TheGame
	 */
	abstract public void setupBeginning();

	/**
	 * doStart keeps track of the game running and sets the current state so it is aware the game is running
	 */
	public void doStart() {
		synchronized(monitor) {

			mLastTime = System.currentTimeMillis() + 100;

			setState(STATE_RUNNING);
			
		}
	}

	/**
	 * run function contains information for the canvas that is able to be drawn upon.
	 */
	@Override
	public void run() {
		Canvas canvasRun;
		while (mRun) {
			canvasRun = null;
			try {
				canvasRun = mSurfaceHolder.lockCanvas(null);
				synchronized (monitor) {
					if (mMode == STATE_RUNNING) {
						updatePhysics();
					}
					doDraw(canvasRun);
				}
			} 
			finally {
				if (canvasRun != null) {
					if(mSurfaceHolder != null)
						mSurfaceHolder.unlockCanvasAndPost(canvasRun);
				}
			}
		}
	}

	public void setSurfaceSize(int width, int height) {
		synchronized (monitor) {
			mCanvasWidth = width;
			mCanvasHeight = height;

			// don't forget to resize the background image
			mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
		}
	}

	/**
	 * doDraw function is run 60(?) times a second so the UI can be consistently kept up to date
	 * most of this function is handled in TheGame this merely returns the canvas so long as it's not null
	 * @param canvas Reference to the canvas that is drawn by this function
	 */
	protected void doDraw(Canvas canvas) {
		
		if(canvas == null) return;

		if(mBackgroundImage != null) canvas.drawBitmap(mBackgroundImage, 0, 0, null);
	}

	/**
	 * updatePhysics is the way that the game knows that time is passing as it updates the global timing
	 * variables of this class
	 */
	private void updatePhysics() {
		now = System.currentTimeMillis();
		elapsed = (now - mLastTime) / 1000.0f;

		updateGame(elapsed);

		mLastTime = now;
	}

	/**
	 * updateGame function is declared as abstract is inside TheGame is where the rules for this function are
	 * defined and implemented
	 * @param secondsElapsed How the function knows that time is passing.
	 */
	abstract protected void updateGame(float secondsElapsed);
	
	/*
	 * Control functions
	 */

	/**
	 * onTouch event is called whenever an area on the canvas is called.
	 *
	 * <p>It defines different actions to perform based on the current state of the game</p>
	 * @param e Reference to the touch on the screen
	 * @return Returns true if the game is not running otherwise false.
	 */
	public boolean onTouch(MotionEvent e) {
		if(e.getAction() != MotionEvent.ACTION_DOWN) return false;
		
		if(mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN) {
			doStart();
			return true;
		}
		
		if(mMode == STATE_PAUSE) {
			unpause();
			return true;
		}
		
		synchronized (monitor) {
				this.actionOnTouch(e.getRawX(), e.getRawY());
		}
		 
		return false;
	}

	/**
	 * Abstract function actionOnTouch that is defined in TheGame so that touches can be handled and
	 * make a difference on the game
	 * @param x x coordinate of the touch event
	 * @param y y coordinate of the touch event
	 */
	abstract protected void actionOnTouch(float x, float y);
	

	/*
	 * Game states
	 */

    /**
     * pause is executed when the game is suspended, it automatically sets the state to STATE_PAUSE
     */
	public void pause() {
		synchronized (monitor) {
			if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
		}
	}

	/**
	 * unpause is executed when the game is resumed, it will catch the clock up to now and set the state
	 * back to STATE_RUNNING
	 */
	public void unpause() {
		// Move the real time clock up to now
		synchronized (monitor) {
			mLastTime = System.currentTimeMillis();
		}
		setState(STATE_RUNNING);
	}	

	//Send messages to View/Activity thread
	public void setState(int mode) {
		synchronized (monitor) {
			setState(mode, null);
		}
	}

	/**
	 * Sets the state based on the passed variables. It's primary function is to contruct the bundles that
	 * will be sent as messages over the handler so that the UI thread can be updated.
	 * @param mode The mode passed to the state e.g. RUNNING
	 * @param message The message to be attached in the handler
	 */
	public void setState(int mode, CharSequence message) {
		synchronized (monitor) {
			mMode = mode;

			if (mMode == STATE_RUNNING || mMode == STATE_READY) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("text", "");
				b.putInt("viz", View.INVISIBLE);
				b.putBoolean("showAd", false);	
				msg.setData(b);
				mHandler.sendMessage(msg);
			} 
			else {				
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				
				Resources res = mContext.getResources();
				CharSequence str = "";
				if (mMode == STATE_READY)
					str = res.getText(R.string.mode_ready);
				else 
					if (mMode == STATE_PAUSE)
						str = res.getText(R.string.mode_pause);
					else 
						if (mMode == STATE_LOSE)
							str = res.getText(R.string.mode_lose);
						else 
							if (mMode == STATE_WIN) {
								str = res.getText(R.string.mode_win);
							}

				if (message != null) {
					str = message + "\n" + str;
				}

				b.putString("text", str.toString());
				b.putInt("viz", View.VISIBLE);

				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}
	}
	
	/*
	 * Getter and setter
	 */	
	public void setSurfaceHolder(SurfaceHolder h) {
		mSurfaceHolder = h;
	}
	
	public boolean isRunning() {
		return mRun;
	}
	
	public void setRunning(boolean running) {
		mRun = running;
	}
	
	public int getMode() {
		return mMode;
	}

	public void setMode(int mMode) {
		this.mMode = mMode;
	}
	
	
	/* ALL ABOUT SCORES */

	/**
	 * setScore will send a message to the UI thread to update whenever the score changes and will
	 * use the handler to accomplish this.
	 * @param score
	 * @param isRedTeam
	 */
	public void setScore(long score, boolean isRedTeam) {
		this.score = score;
		
		synchronized (monitor) {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putBoolean("score", true);
			b.putBoolean("isRedTeam", isRedTeam);
			b.putString("text", getScoreString().toString());
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}

	public float getScore() {
		return score;
	}
	
//	public void updateScore(long score) {
//		this.setScore(this.score + score);
//	}
	
	
	protected CharSequence getScoreString() {
		return Long.toString(Math.round(this.score));
	}
	
}