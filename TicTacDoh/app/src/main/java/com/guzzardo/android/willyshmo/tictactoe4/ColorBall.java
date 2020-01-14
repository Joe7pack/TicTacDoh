package com.guzzardo.android.willyshmo.tictactoe4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

import com.guzzardo.android.willyshmo.tictactoe4.GameView.ScreenOrientation;

public class ColorBall { 
	
	private Bitmap img; // the image of the ball
	private int coordX; // the current x coordinate at the canvas
	private int coordY; // the current y coordinate at the canvas
	private int startingLandscapeCoordX; // the starting x coordinate at the canvas
	private int startingLandscapeCoordY; // the starting y coordinate at the canvas
	private int startingPortraitCoordX; // the starting x coordinate at the canvas
	private int startingPortraitCoordY; // the starting y coordinate at the canvas
	private int mDisplayMode; //portrait or landscape
	
	private int id; // gives every ball its own id
	private int mType; // circle, cross or circleCross token
	private boolean mDisabled; //indicates ball can no longer be moved (finalized placement on board)
	
	private static int count = 0;
	private static final int MAXBALLS = 8;
 
	public final static int CIRCLE = 0;
	public final static int CROSS = 1;
	public final static int CIRCLECROSS = 2;
 
	public void updateBall(Context context, int type, Bitmap bitmap) {
		mType = type;
		img = bitmap;
		resetPosition(mDisplayMode);
		mDisabled = false;
	}
	
	public static void setTokenColor(Bitmap img, int newColor) {
		int width = img.getWidth();
		int height = img.getHeight();

		int[] pixels = new int[width * height];
		img.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int x = 0; x < pixels.length; x++) {
			if (pixels[x] != 0) {
				pixels[x] = newColor;
			}
		}
		
		img.setPixels(pixels, 0, width, 0, 0, width, height);
		
	}
	
	public ColorBall(Context context, int drawable, Point pointLandscape, Point pointPortrait, int displayMode, int type, int color) {
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//      opts.inJustDecodeBounds = true;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        img = BitmapFactory.decodeResource(context.getResources(), drawable, bitmapOptions); 
        setTokenColor(img, color);        
        
        id=count;
		count++;
		if (count > MAXBALLS - 1)
			count = 0;
		if (displayMode == ScreenOrientation.LANDSCAPE) {
			coordX= pointLandscape.x;
			coordY = pointLandscape.y;
		} else {
			coordX= pointPortrait.x;
			coordY = pointPortrait.y;
		}
		startingLandscapeCoordX = pointLandscape.x;
		startingLandscapeCoordY = pointLandscape.y;
		startingPortraitCoordX = pointPortrait.x;
		startingPortraitCoordY = pointPortrait.y;
		
		mType = type;
		mDisplayMode = displayMode;
	}
	
	public int getType() {
		return mType;
	}
	
	public static int getCount() {
		return count;
	}
	
	void setX(int newValue) {
		coordX = newValue;
    }
	
	public int getX() {
		return coordX;
	}

	void setY(int newValue) {
		coordY = newValue;
   }
	
	public int getY() {
		return coordY;
	}
	
	public int getID() {
		return id;
	}

	public static int getMaxBalls() {
		return MAXBALLS;
	}
	
	public Bitmap getBitmap() {
		return img;
	}

	 public boolean isDisabled() {
		return mDisabled;
	}

	public void setDisabled(boolean disabled) {
		mDisabled = disabled;
	}
	
	public void resetPosition(int displayMode) {
		if (displayMode == ScreenOrientation.LANDSCAPE) {
			coordX = startingLandscapeCoordX;
			coordY = startingLandscapeCoordY;
		} else {
			coordX = startingPortraitCoordX;
			coordY = startingPortraitCoordY;
		}
	}

	public Rect getRect() {
		return new Rect(coordX, coordY, 0, 0);
	}
	
//	public void moveBall(int goX, int goY) {
//		// check the borders, and set the direction if a border has reached
//		if (coordX > 250){
//			goRight = false;
//		}
//		if (coordX < 0){
//			goRight = true;
//		}
//		if (coordY > 250){
//			goDown = false;
//		}
//		if (coordY < 0){
//			goDown = true;
//		}
//		// move the x and y 
//		if (goRight){
//			coordX += goX;
//		}else
//		{
//			coordX -= goX;
//		}
//		if (goDown){
//			coordY += goY;
//		}else
//		{
//			coordY -= goY;
//		}
//	}
	
//	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//		try {
//			img.compress(Bitmap.CompressFormat.PNG, 0, out);
//			out.writeInt(coordX);
//			out.writeInt(coordY);
//			out.writeInt(id);
//			out.writeInt(mType);
//			out.writeBoolean(mDisabled);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new IOException(e.toString());
//		}
//	}
//
//	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
//		try {
//			img = BitmapFactory.decodeStream(in);
//			coordX = in.readInt();
//			coordY = in.readInt();
//			id = in.readInt();
//			mType = in.readInt();
//			mDisabled = in.readBoolean();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new IOException(e.toString());
//		}
//	}
	
}
