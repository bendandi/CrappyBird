package com.example.drawing;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.crappybird.R;

public class GameBoard extends View {
	
	//what we draw with
	private Paint p;
	//pipes
	private int pipeADistFromLeft = -150;
	private int pipeBDistFromLeft = -150;
	private Random r = new Random();
	private int gapAStart;
	private int gapBStart;
	
	private int score = 0;
	// birdy
	private Point sprite1;
	private Bitmap sprite1BM = null;
	private Matrix rotatorMatrix;
	private int spriteRotationAngle = 360;
	//0 = neutral flight 1=upwards -1=descending
	private int isGoingDown = 0;

	private boolean collisionDetected = false;
	
	//encouraging remarks
	private Bitmap encouragingRemark1 = null;
	private Bitmap encouragingRemark2 = null;
	
	private boolean pipesPaused = true;

	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);

		p = new Paint();
		sprite1 = new Point(getWidth()/3, getHeight()/2);
		sprite1BM = BitmapFactory.decodeResource(getResources(),
				R.drawable.pajaro);
		
		encouragingRemark1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ohwow);
		encouragingRemark2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.success);
		
		
		//not used atm...for flight animation
		rotatorMatrix = new Matrix();
		isGoingDown = 0;

		
	}

	synchronized public boolean getCollisionDetected() {
		return collisionDetected;
	}
	
	synchronized public void setCollisionDetected(boolean collision) {
		 collisionDetected = collision;
	}

	synchronized public void setSprite1(int x, int y) {
		sprite1 = new Point(x, y);
	}

	synchronized public void setSprite1Y(int y) {
		sprite1.y = y;
	}

	synchronized public int getSprite1Y() {
		return sprite1.y;
	}

	synchronized public int getSprite1Bottom() {
		return sprite1.y + sprite1BM.getHeight();
	}

	synchronized public void setPipeADistFromLeft(int distance) {
		pipeADistFromLeft = distance;
	}
	
	synchronized public void setPipeBDistFromLeft(int distance) {
		pipeADistFromLeft = distance;
	}
	
	synchronized public void setPipesPaused(boolean paused) {
		pipesPaused = paused;
	}
	
	synchronized public void setIsGoingDown(int direction){
		isGoingDown = direction;
	}
	
	synchronized public void setRotationAngle(int degrees){
		spriteRotationAngle = degrees;
	}
	synchronized public int getScore(){
		return score;
	}
	
	


	private boolean checkForCollision() {
		/*sprite x and y points not on canvas?
		if (sprite1.x < 0 && sprite2.x < 0 && sprite1.y < 0 && sprite2.y < 0)
			return false;*/

		//make a box around the sprite
		Rect r1 = new Rect(sprite1.x, sprite1.y, sprite1.x + sprite1BM.getWidth(), sprite1.y + sprite1BM.getHeight());
		//same for pipes
		Rect r2 = new Rect(pipeADistFromLeft, 0, pipeADistFromLeft+150, gapAStart);
		Rect r3 = new Rect(pipeADistFromLeft, gapAStart+400, pipeADistFromLeft+150, getHeight());
		Rect r4 = new Rect(pipeBDistFromLeft, 0, pipeBDistFromLeft+150, gapBStart);
		Rect r5 = new Rect(pipeBDistFromLeft, gapBStart+400, pipeBDistFromLeft+150, getHeight());

		Rect r6 = new Rect(r1);
		
		Rect r7 = new Rect();
		//if bird rectancle intersect any of the pipe rectangles pick that pipe...
		if(r1.intersect(r2))r7 = r2;
		else if(r1.intersect(r3))r7 = r3;
		else if(r1.intersect(r4))r7 = r4;
		else if(r1.intersect(r5))r7 = r5;
		
		
		// collision algorithm adapted from
		//http://www.techrepublic.com/blog/software-engineer/the-abcs-of-android-game-development-detect-collisions/
		//...check for overlap then check if overlapping pixels are transparent
		if (!r7.isEmpty()) {
			for (int i = r1.left; i < r1.right; i++) {
				for (int j = r1.top; j < r1.bottom; j++) {
					if (sprite1BM.getPixel(i - r6.left, j - r6.top) != Color.TRANSPARENT) {
						sprite1BM = BitmapFactory.decodeResource(getResources(),
								R.drawable.pajarotear);
							return true;
					}
				}
			}
		}

		return false;
	}
	
	private void scoreKeeper(){
		if( pipeADistFromLeft+ 152 == sprite1.x | pipeBDistFromLeft+ 152 == sprite1.x)
			score++;
	}
//for performance generally avoid initialising objects in ondraw
	
	@Override
	synchronized public void onDraw(Canvas canvas) {

		// fill screen with background color and set alpha level
		p.setColor(Color.LTGRAY);
		p.setAlpha(255);

		p.setStrokeWidth(1);
		// draw rectangle left, top, right, bottom, PaintObject
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);

		// pipe A stuff if off screen to the left put it back to start at other side...
		if (pipeADistFromLeft == -150) {
			pipeADistFromLeft = getWidth();
			//randomize gap in pipes
			// random range.. (max - min)+ min
			gapAStart = r.nextInt((getHeight() - 500) - 100) + 100;
		}
		p.setColor(Color.DKGRAY);
		p.setStrokeWidth(1);
		//upper part of pipe
		canvas.drawRect(pipeADistFromLeft, 0, pipeADistFromLeft + 150,
				gapAStart, p);
		//lower part of pipe
		canvas.drawRect(pipeADistFromLeft, gapAStart + 400,
				pipeADistFromLeft + 150, getHeight()-200, p);
		
		if(!pipesPaused)
		pipeADistFromLeft -= 2;

		// pipe b stuff
		if (pipeADistFromLeft == (getWidth() / 2) - 74) {
			pipeBDistFromLeft = getWidth();
			// random (max - min)+ min
			gapBStart = r.nextInt((getHeight() - 500) - 100) + 100;
		}
		canvas.drawRect(pipeBDistFromLeft, 0, pipeBDistFromLeft + 150,
				gapBStart, p);
		canvas.drawRect(pipeBDistFromLeft, gapBStart + 400,
				pipeBDistFromLeft + 150, getHeight()-200, p);
		
		if(!pipesPaused)
		pipeBDistFromLeft -= 2;
		
		//draw ground
		p.setColor(Color.GREEN);
		canvas.drawRect(0, getHeight()- 200, getWidth(),getHeight(),  p);
		
		//Write score
				p.setColor(Color.YELLOW);
				p.setTextSize(100);
				canvas.drawText(Integer.toString(score), getWidth()-100, 100,p);

		// ocean motion for pichting around while flying and falling
		/*	not functional yet
		if(isGoingDown == -1){
			rotatorMatrix.reset();
			rotatorMatrix.postTranslate((float) (sprite1.x), (float) (sprite1.y));
			rotatorMatrix.postRotate(spriteRotationAngle,
					(float)sprite1.x ,
					(float) sprite1.y + sprite1BM.getHeight() );
			canvas.drawBitmap(sprite1BM, rotatorMatrix, null);
			spriteRotationAngle += 4;
			if(spriteRotationAngle >= 360)
				spriteRotationAngle = spriteRotationAngle - 360;
			
		}
		else if(isGoingDown == 1){
			
			rotatorMatrix.reset();
			rotatorMatrix.postTranslate((float) (sprite1.x), (float) (sprite1.y));
			rotatorMatrix.postRotate(spriteRotationAngle,
					(float) sprite1.x ,
					(float) sprite1.y);
			canvas.drawBitmap(sprite1BM, rotatorMatrix, null);
			spriteRotationAngle -= 2;
			if (spriteRotationAngle <= 320)
				spriteRotationAngle= 320;
			
			
		}
		else{
			
			canvas.drawBitmap(sprite1BM, sprite1.x, sprite1.y, p);
		}*/
		
		//draw birdy
		canvas.drawBitmap(sprite1BM, sprite1.x, sprite1.y, p);
		//switch out engouraging remarks at various points
		switch(score){
		case 1:
			if(pipeADistFromLeft+151 > sprite1.x-40 && pipeADistFromLeft < sprite1.x)
			canvas.drawBitmap(encouragingRemark1 ,sprite1.x + sprite1BM.getWidth(), sprite1.y - encouragingRemark1.getHeight(), p);
			break;
		case 3:
			if(pipeADistFromLeft+151 > sprite1.x-60 && pipeADistFromLeft < sprite1.x)
			canvas.drawBitmap(encouragingRemark2 ,sprite1.x + sprite1BM.getWidth(), sprite1.y - encouragingRemark2.getHeight(), p);
			break;
		}
		// check for collision
		collisionDetected = checkForCollision();
		if(collisionDetected)pipesPaused = true;
		//keep score
		scoreKeeper();

	}
	

}
