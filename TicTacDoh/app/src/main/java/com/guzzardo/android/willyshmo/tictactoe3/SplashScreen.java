package com.guzzardo.android.willyshmo.tictactoe3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

//import android.support.multidex.MultiDex;

public class SplashScreen extends Activity implements ToastMessage {
    protected boolean mActive = true;
    private static boolean mSkipWait;
    private static int mSplashTime = 2500;
    public static ErrorHandler mErrorHandler;
    private static Resources mResources;
	private ProgressDialog mPrizeWaitDialog;

/*
    @Override
   protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
*/
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mResources = getResources();
        mErrorHandler = new ErrorHandler(); 
        boolean mPrizesAvailable = false;
        if ("true".equalsIgnoreCase(mResources.getString(R.string.prizesAvailable))) {
        	mPrizesAvailable = true;
        }
        
        WillyShmoApplication.setLatitude(0);
        WillyShmoApplication.setLongitude(0);
        
        if (mPrizesAvailable) {
        	new LoadPrizesTask().execute(SplashScreen.this, getApplicationContext(), getResources());
        	mSkipWait = true;
        }
        
//        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound_file_1);
//        mp.start();

        
        
        // thread for displaying the SplashScreen
//        Thread splashThread = new Thread() {
        class SplashThread extends Thread {	
        	boolean mPrizesAvailable;
        	
        	void setPrizesAvailable(boolean prizesAvailable) {
        		mPrizesAvailable = prizesAvailable;
        	}
        	
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(mActive && (waited < mSplashTime || mSkipWait)) {
//                  while(mActive) {
                        sleep(100);
                        if(mActive) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                	finish();
                	if (!mPrizesAvailable) {
                		WillyShmoApplication.setNetworkAvailable(true);
                		startActivity(new Intent("com.guzzardo.android.willyshmo.tictactoe3.MainActivity"));
                	}
                }
            }
        };
        
        SplashThread splashThread = new SplashThread();
        splashThread.setPrizesAvailable(mPrizesAvailable);
        splashThread.start();
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//          mActive = false;
            setSplashActive(false);
        }
        return true;
    }
    
    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
    		Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
    
    public void sendToastMessage(String message) {
    	Message msg = SplashScreen.mErrorHandler.obtainMessage();
    	msg.obj = message;
    	SplashScreen.mErrorHandler.sendMessage(msg);	
    }
    
    public void showGooglePlayError(final Integer isPlayAvailable, final String playErrorMessage) {
    	try {
    		AlertDialog dialog = createGooglePlayErrorDialog(isPlayAvailable, playErrorMessage);
        	dialog.show();
    	} catch (Exception e) {
    		sendToastMessage(e.getMessage());    		
    	}
    }
    
	public AlertDialog createGooglePlayErrorDialog(final Integer isPlayAvailable, final String playErrorMessage) {
        return new AlertDialog.Builder(SplashScreen.this)
        .setIcon(R.drawable.willy_shmo_small_icon)
        .setTitle(R.string.google_play_service_error)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked OK so do some stuff */
            	callGooglePlayServicesUtil(isPlayAvailable.intValue());
            	setSplashActive(false);
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked Cancel so do some stuff */
            	setSplashActive(false);
            }
        })
        .setMessage(playErrorMessage)
        .create();
	}
	
	private void callGooglePlayServicesUtil(int isPlayAvailable) {
		GooglePlayServicesUtil.getErrorDialog(isPlayAvailable, SplashScreen.this, 99);
	}
    
	public void setSplashActive(boolean active) {
		mActive = active;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		setSplashActive(false);
		if (null != mPrizeWaitDialog) {
			mPrizeWaitDialog.dismiss();
		}
	}
	
	public void startWaitForPrizesPopup() { 
		mPrizeWaitDialog = createPrizeWaitDialog();
	}

	private ProgressDialog createPrizeWaitDialog() {
		return ProgressDialog.show(SplashScreen.this, "Woohoo! You can win prizes!", "Please wait while prize list is loaded", false, false);
	}
	
}