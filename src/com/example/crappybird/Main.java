//Currently quite disfunctional attempt at a flappy bird clone. 
//needs work on collision detection and animation but basic concept is functional
//basic concepts of Android game development were learned by going through the tutorial linked below,
//the basic architecture and collision algorithm are derived from there.
//http://www.techrepublic.com/blog/software-engineer/the-abcs-of-android-game-development-prepare-the-canvas/

package com.example.crappybird;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.drawing.GameBoard;
//Main handles all functionality and input handling. GameBoard only displays the view.
public class Main extends Activity {

	private Handler frame = new Handler();
	private final int FRAME_RATE = 20;
	// midpoint of stable flight
	private int stableY;
	//+ or - pixels y per frame update
	private int bounceDirection = 2;
	// can be moved into method
	private int changeInLevel;
	private boolean gameStarted;


	// Initial pixels y per frame update on tap 
	private int speed = -16;

	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		synchronized public boolean onTouch(View v, MotionEvent event) {
			//handle first tap - start pipes...
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (gameStarted == false) {
					gameStarted = true;
					((GameBoard) findViewById(R.id.the_canvas))
							.setPipesPaused(false);
				}
			}
			//handle subsequent taps
			if (((GameBoard) findViewById(R.id.the_canvas))
					.getCollisionDetected() == false && ((GameBoard) findViewById(R.id.the_canvas)).getSprite1Y() > 1) {

				speed = -16;
			}
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//GameBoard = custom View to display game
		((GameBoard) findViewById(R.id.the_canvas))
				.setOnTouchListener(touchListener);

		Handler h = new Handler();

		// We can't initialise the graphics immediately because the layout manager
		// needs to run first, so we call back in a second.
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				initialiseGraphics();
			}

		}, 1000);
	}

	//graphics are initialised
	synchronized public void initialiseGraphics() {
		
		gameStarted=false;
		//set bird initial position
		int x = ((GameBoard) findViewById(R.id.the_canvas)).getWidth();
		int y = ((GameBoard) findViewById(R.id.the_canvas)).getHeight();
		stableY = y / 2;
		((GameBoard) findViewById(R.id.the_canvas)).setSprite1(x / 3, y / 2);

		// remove any existing callbacks to keep them from stacking up
		frame.removeCallbacks(frameUpdate);

		((GameBoard) findViewById(R.id.the_canvas)).invalidate();

		frame.postDelayed(frameUpdate, FRAME_RATE);
	}
	//Graphics updates for each frame update/ game actions
	private Runnable frameUpdate = new Runnable() {

		@Override
		synchronized public void run() {
			// check for collision in last frame refresh
			if (((GameBoard) findViewById(R.id.the_canvas)).getCollisionDetected() == true) {
				if(((GameBoard) findViewById(R.id.the_canvas)).getSprite1Bottom() >= ((GameBoard) findViewById(R.id.the_canvas)).getHeight()-190)
					return;
				
				((GameBoard) findViewById(R.id.the_canvas)).setPipesPaused(true);
				((GameBoard) findViewById(R.id.the_canvas)).setSprite1Y(((GameBoard) findViewById(R.id.the_canvas)).getSprite1Y() + speed);

				speed += 2;
		
				if(((GameBoard) findViewById(R.id.the_canvas)).getSprite1Bottom() >= ((GameBoard) findViewById(R.id.the_canvas)).getHeight())
					return;
			}

			if(!gameStarted)
			bounceSprite();

			if (gameStarted) {
				flightPattern();
			}
			// make any updates to on screen objects here
			// then invoke the ondraw by invalidating the canvas
			((GameBoard) findViewById(R.id.the_canvas)).invalidate();
			frame.postDelayed(frameUpdate, FRAME_RATE);
		}

	};

	// have the sprite bounce up and down before user input
	private void bounceSprite() {

		changeInLevel = ((GameBoard) findViewById(R.id.the_canvas))
				.getSprite1Y() - stableY;
		((GameBoard) findViewById(R.id.the_canvas))
				.setSprite1Y(((GameBoard) findViewById(R.id.the_canvas))
						.getSprite1Y() + bounceDirection);

		if (changeInLevel > 10) {
			bounceDirection = -2;
		} else if (changeInLevel < -10) {
			bounceDirection = 2;
		}
	}

	//Take flight
	public void flightPattern() {

		((GameBoard) findViewById(R.id.the_canvas))
				.setSprite1Y(((GameBoard) findViewById(R.id.the_canvas))
						.getSprite1Y() + speed);

		speed += 2;		

		if (((GameBoard) findViewById(R.id.the_canvas)).getSprite1Y() <= 0) {
			speed = 0;
		}

		if (((GameBoard) findViewById(R.id.the_canvas)).getSprite1Bottom() >= ((GameBoard) findViewById(R.id.the_canvas)).getHeight()-190 ) {
			((GameBoard) findViewById(R.id.the_canvas))
					.setCollisionDetected(true);
			speed = 0;
		}
		
		//messing around with flight animation...not functioning atm
		/*if(speed < 16){
			
			//((GameBoard) findViewById(R.id.the_canvas)).setIsGoingDown(-1);
			//((GameBoard) findViewById(R.id.the_canvas)).setIsGoingDown(1);
		}
		if(speed < 0){
			//((GameBoard) findViewById(R.id.the_canvas)).setRotationAngle(360);
			//((GameBoard) findViewById(R.id.the_canvas)).setIsGoingDown(1);
		}*/

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle input to the action bar items--restart game
		
	    switch (item.getItemId()) {
	        case R.id.restart:
	            onCreate(null);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	

}
