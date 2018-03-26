package uk.ac.reading.ps026478.playgroundwars;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * GameView class inherits features from the SurfaceView so that the game view UI can be created.
 *
 * @author Joe Fazzino
 * @version 1.0
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private volatile GameThread thread;

	//private SensorEventListener sensorAccelerometer;

	//Handle communication from the GameThread to the View/Activity Thread
	private Handler mHandler;
	
	//Pointers to the views
	private TextView mScoreView;
	private TextView mStatusView;
	private TextView mPlayerName;
	private Button mEndTurnButton;

	private String mPlayer1Name, mPlayer2Name;

	private int mMessageCount = 1;

	/**
	 * Constructor for the GameView class, sets up handler for recieving messages
	 * from the game thread so that it can manipulate the UI thread
	 *
	 * @param context
	 * @param attrs
	 */
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		//Set up a handler for messages from GameThread
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message m) {
				if(m.getData().getBoolean("score")) {
					if (m.getData().getBoolean("isRedTeam")) {
						mScoreView.setTextColor(getResources().getColor(R.color.red_team));
						mPlayerName.setTextColor(getResources().getColor(R.color.red_team));
						if (mMessageCount == 1) {
							mPlayerName.setText("Red Goes First");
							mMessageCount++;
						} else {
							mPlayerName.setText(mPlayer1Name);
						}
					} else {
						mScoreView.setTextColor(getResources().getColor(R.color.blue_team));
						mPlayerName.setTextColor(getResources().getColor(R.color.blue_team));
						mPlayerName.setText(mPlayer2Name);
					}
					mScoreView.setText(m.getData().getString("text"));
				}
				else {
					//So it is a status
                    int i = m.getData().getInt("viz");
                    switch(i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }

                    mStatusView.setText(m.getData().getString("text"));
				}
			}
		};
	}


	/**
	 * Command is run in order to release any resources when the app is destroyed
	 */
	public void cleanup() {
		this.thread.setRunning(false);
		this.thread.cleanup();
		
		this.removeCallbacks(thread);
		thread = null;
		
		this.setOnTouchListener(null);
		
		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}
	
	/*
	 * Setters and Getters
	 */

	public void setThread(GameThread newThread) {

		thread = newThread;

		setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
		});

        setClickable(true);
		setFocusable(true);
	}
	
	public GameThread getThread() {
		return thread;
	}

	public TextView getStatusView() {
		return mStatusView;
	}

	public void setEndTurnButton(Button mEndTurnButton) {
		this.mEndTurnButton = mEndTurnButton;
	}

	public void setPlayerName(TextView mPlayerName) {
		this.mPlayerName = mPlayerName;
	}

	public void setStatusView(TextView mStatusView) {
		this.mStatusView = mStatusView;
	}

	public void setPlayer1Name(String mPlayer1Name) {
		this.mPlayer1Name = mPlayer1Name;
	}

	public void setPlayer2Name(String mPlayer2Name) {
		this.mPlayer2Name = mPlayer2Name;
	}

	public TextView getScoreView() {
		return mScoreView;
	}

	public void setScoreView(TextView mScoreView) {
		this.mScoreView = mScoreView;
	}
	

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}


	/**
	 * onWindowFocus change command checks to see if the window is no longer in focus
	 * and if it's not then pause the running game thread.
	 * @param hasWindowFocus Checks to see if the window is in focus
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(thread!=null) {
			if (!hasWindowFocus)
				thread.pause();
		}
	}

	/**
	 * Starts up the game thread if the surface has been created.
	 * @param holder Reference to the surface holder
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		if(thread!=null) {
			thread.setRunning(true);
			
			if(thread.getState() == Thread.State.NEW){
				//Just start the new thread
				thread.start();
			}
			else {
				if(thread.getState() == Thread.State.TERMINATED){
					//Start a new thread
					//Should be this to update screen with old game: new GameThread(this, thread);
					//The method should set all fields in new thread to the value of old thread's fields 
					thread = new TheGame(this, mEndTurnButton, mPlayerName);
					thread.setRunning(true);
					thread.start();
				}
			}
		}
	}
	
	//Always called once after surfaceCreated. Tell the GameThread the actual size

	/**
	 * surfaceChanged function is always called once after ther surfaceCreated function has run,
	 * it informs the game thread of the size of the surface.
	 * @param holder Reference to the surface holder
	 * @param format Format of the surface
	 * @param width Width of the surface
	 * @param height Height of the surface
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(thread!=null) {
			thread.setSurfaceSize(width, height);			
		}
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused on even stopped.
	 */

	/**
	 * surfaceDestroyed stops the GameThread from running if the surface is destroyed.
	 * @param arg0
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		boolean retry = true;
		if(thread!=null) {
			thread.setRunning(false);
		}
		
		//join the thread with this thread
		while (retry) {
			try {
				if(thread!=null) {
					thread.join();
				}
				retry = false;
			} 
			catch (InterruptedException e) {
				//naugthy, ought to do something...
			}
		}
	}
}
