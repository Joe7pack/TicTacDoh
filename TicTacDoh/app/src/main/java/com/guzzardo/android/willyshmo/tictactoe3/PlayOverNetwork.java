package com.guzzardo.android.willyshmo.tictactoe3;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.guzzardo.android.willyshmo.tictactoe3.MainActivity.UserPreferences;

public class PlayOverNetwork extends Activity implements ToastMessage {
	
    public interface NetworkValues {
//    	static final String domainName = "http://ww2.guzzardo.com:8081"; // for production
//    	static final String domainName = "http://localhost:8092";
//    	static final String domainName = "http://10.0.2.2:8092"; // this works ok when using the emulator but when using
    	                                                         // the G1 phone i need to use an external ip address
    															 // e.g. ww2.guzzardo.com or testandroid.guzzardo.com
//    	static final String domainName = "http://testandroid.guzzardo.com:8082"; // for test 
//    	static final String domainName = "http://216.80.121.243:6999"; // for test 
    	
    	
//    	static final String domainName = "http://willyshmotest.guzzardo.com"; // for test - link up to Grails - set to port 6260       	
//    	static final String domainName = "http://willyshmoprod.guzzardo.com"; // for Prod - link up to Grails - set to port 6360     	
    }

	//TODO - need to have an option to reset Twitter Credentials
	
    private static int mPlayer1Id;
	private String mPlayer1Name;    
    private static Resources resources;
    public static ErrorHandler errorHandler;
//  private InitializeOAuthResultSet initializeOAuthResults;
//	private FinalizeOAuthResultTask finalizeOAuthResultTask;
//	private String accessToken, accessTokenSecret, screenName, userID;
//	private String twitterMessage; // twitterResponse;
//	private String mOAuthInitialized, mUserId;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorHandler = new ErrorHandler();
        
        resources = getResources();
        getSharedPreferences();
        
        if (mPlayer1Name == null) {
            mPlayer1Name = getIntent().getStringExtra(GameActivity.PLAYER1_NAME);
        }
        
//        Log.d(TAG, "onCreate called accesToken: " + accessToken);        
        
//        if (mOAuthInitialized == null) {
//    		try {
//    			Log.d(TAG, "onCreate calling OAuth");      			
//    			final TembooSession tembooSession = new TembooSession(AuthenticationValues.getTembooAccountName(), 
//    				AuthenticationValues.getTembooAppKeyName(), AuthenticationValues.getTembooAppKeyValue());
//    			InitializeOAuthResultTask initializeOAuthResultTask = new InitializeOAuthResultTask();
//    			initializeOAuthResultTask.execute(this, tembooSession, getApplicationContext());
//    		} catch (Exception e) {
//    			sendToastMessage(e.getMessage());        	
//    		}
//      }

        if (mPlayer1Id == 0) {
        	setSharedPreferences(); 
        	addMyselfToPlayerList();
        } else {
        	WebServerInterfaceUsersOnlineTask webServerInterfaceUsersOnlineTask = new WebServerInterfaceUsersOnlineTask();
        	webServerInterfaceUsersOnlineTask.execute(this, getApplicationContext(), mPlayer1Name, resources, Integer.valueOf(mPlayer1Id));
        }
    	finish();
    }
    
    private void addMyselfToPlayerList() {
    	// add a new entry to the GamePlayer table
    	String androidId = "?deviceId=" + WillyShmoApplication.getAndroidId(); 
    	String latitude = "&latitude=" + WillyShmoApplication.getLatitude();
    	String longitude = "&longitude=" + WillyShmoApplication.getLongitude();
    	String trackingInfo = androidId + latitude + longitude;
    	
    	String url = resources.getString(R.string.domainName) + "/gamePlayer/createAndroid/" + trackingInfo + "&userName=";
    	WebServerInterfaceNewPlayerTask webServerInterfaceNewPlayerTask = new WebServerInterfaceNewPlayerTask();
    	final PlayOverNetwork playOverNetwork = this;
    	webServerInterfaceNewPlayerTask.execute(playOverNetwork, url, mPlayer1Name, getApplicationContext(), resources);
    }
    
    private void getSharedPreferences() {
        SharedPreferences settings = getSharedPreferences(UserPreferences.PREFS_NAME, MODE_PRIVATE);
        mPlayer1Id = settings.getInt(GameActivity.PLAYER1_ID, 0);
        mPlayer1Name = settings.getString(GameActivity.PLAYER1_NAME, null);
//		accessToken = settings.getString("ga_accessToken", null);
//		accessTokenSecret = settings.getString("ga_accessTokenSecret", null);		
//		mScreenName = settings.getString("ga_screenName", null);		
//		mUserId = settings.getString("ga_userID", null);	
//		mOAuthInitialized = settings.getString("ga_initalizeOAuth", null); 		
    }
    
    private void setSharedPreferences() {
    	SharedPreferences settings = getSharedPreferences(UserPreferences.PREFS_NAME, MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString(GameActivity.PLAYER1_NAME, mPlayer1Name);
    	// Commit the edits!
    	editor.commit();  
    }
    
    @Override
    protected void onPause() {
        super.onPause();  
    }
    
    @Override
    protected void onResume() {
        super.onResume();   
        mPlayer1Name = getIntent().getStringExtra(GameActivity.PLAYER1_NAME);        
    }    

    @Override
    protected void onStop() {
        super.onStop();
    }    

    @Override
    protected void onStart() {
        super.onStart();
    }    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }    
    
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("gn_player1_Id", mPlayer1Id);
	}

	@Override	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mPlayer1Id = savedInstanceState.getInt("gn_player1_Id");
	}    
	
	/**
     * A simple utility Handler to display an error message as a Toast popup
     * @param errorMessage
     */
    
    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
    		Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
    
//	public String getTembooAccountName() {
//		return tembooAccountName;
//	}
//
//	public String getTembooAppKeyName() {
//		return tembooAppKeyName;
//	}
//
//	public String getTembooAppKeyValue() {
//		return tembooAppKeyValue;
//	}
	
//    public void setInitializeOAuthResultSet(InitializeOAuthResultSet initializeOAuthResults) {
//    	this.initializeOAuthResults = initializeOAuthResults;
//    }
//    
//    public InitializeOAuthResultSet getInitializeOAuthResultSet() {
//    	return initializeOAuthResults;
//    }
//    
//    public FinalizeOAuthResultTask getFinalizeOAuthResultTask() {
//    	finalizeOAuthResultTask = new FinalizeOAuthResultTask();
//    	return finalizeOAuthResultTask;
//    }
    
//    public void setAccessToken(String accessToken) {
//    	this.accessToken = accessToken;
//    }
//    
//    public String getAccessToken() {
//    	return this.accessToken;
//    }
//    
//    public void setAccessTokenSecret(String accessTokenSecret) {
//    	this.accessTokenSecret = accessTokenSecret;
//    }
//
//    public String getAccessTokenSecret() {
//    	return this.accessTokenSecret;
//    }
    
//    public String getTwitterConsumerKey() {
//    	return twitterConsumerKey;
//    }
//    
//    public String getTwitterConsumerSecret() {
//    	return twitterConsumerSecret;
//    }
    
//    public String getScreenName() {
//		return screenName;
//	}
//
//	public void setScreenName(String screenName) {
//		this.screenName = screenName;
//	}
//
//	public String getUserID() {
//		return userID;
//	}
//
//	public void setUserID(String userID) {
//		this.userID = userID;
//	}
    
//	public String getTwitterMessage() {
//		return twitterMessage;
//	}
//
//	public void setTwitterMessage(String twitterMessage) {
//		this.twitterMessage = twitterMessage;
//	}

    public void sendToastMessage(String message) {
    	Message msg = PlayOverNetwork.errorHandler.obtainMessage();
    	msg.obj = message;
    	PlayOverNetwork.errorHandler.sendMessage(msg);	
    }
	
}
