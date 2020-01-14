package com.guzzardo.android.willyshmo.tictactoe4;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.math.BigDecimal;

public class LazyAdapter extends BaseAdapter implements ToastMessage {
    
    private Activity activity;
    private Resources resources;
    private String[] imageDescription;
    private Bitmap[] imageBitmap;
    private String[] prizeDistance;
    private String[] prizeLocation;  
    private String[] imageWidth;
    private String[] imageHeight;
    
    private static LayoutInflater inflater=null;
    public static ErrorHandler errorHandler;      
    
    public LazyAdapter(Activity a, String[] desc, Bitmap[] image, String[] imageWidth, String[] imageHeight,
                       String[] prizeDistance, String[] prizeLocation, Resources resources) {
        activity = a;
        imageDescription = desc;
        this.imageBitmap = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.prizeDistance = prizeDistance; 
        this.prizeLocation = prizeLocation;
        this.resources = resources;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return imageDescription.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		try {
			if (convertView == null) {
				vi = inflater.inflate(R.layout.prizes, null);
			}
		} catch (Exception e) {
			sendToastMessage("Lazy adapter inflater error: " + e.getMessage());
//			System.out.println("convert View: " + e.getMessage());
		}

        TextView text = (TextView)vi.findViewById(R.id.prize_description);
        text.setText(imageDescription[position]);
        text.setBackgroundColor(Color.LTGRAY);
        ImageView image = (ImageView)vi.findViewById(R.id.prize_image);
        
        int width = Integer.valueOf(imageWidth[position]);
        int height = Integer.valueOf(imageHeight[position]);
        
        image.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        image.setImageBitmap(imageBitmap[position]);
        TextView textDistance = (TextView)vi.findViewById(R.id.prize_distance);
        
        if (prizeLocation[position].equals("1")) {
        	String distance = prizeDistance[position];
        	BigDecimal decimal = new BigDecimal(distance);
        	decimal = decimal.setScale(2, BigDecimal.ROUND_UP);
        	textDistance.setText(decimal.toString());
        } else if (prizeLocation[position].equals("0")) {
        	textDistance.setText(resources.getString(R.string.not_applicable));
        } else if (prizeLocation[position].equals("2")) {
        	textDistance.setText(resources.getString(R.string.multiple_locations));
        } else {
        	textDistance.setText("???");
        }
        return vi;
    }
    
	@Override
	public void sendToastMessage(String message) {
    	Message msg = LazyAdapter.errorHandler.obtainMessage();
    	msg.obj = message;
    	LazyAdapter.errorHandler.sendMessage(msg);	
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
	}
	
    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
    		Toast.makeText(activity.getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
    
    public void startActivity(Intent i) {
    	
    }
    
}
