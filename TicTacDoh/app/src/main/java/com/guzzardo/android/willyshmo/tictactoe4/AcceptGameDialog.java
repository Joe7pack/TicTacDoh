package com.guzzardo.android.willyshmo.tictactoe4;

//This class is no longer used, GameActivity.acceptIncomingGameRequestFromClient() is used instead

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.guzzardo.android.willyshmo.tictactoe4.MainActivity.UserPreferences;

import androidx.appcompat.app.AlertDialog;

//import android.support.v7.app.AlertDialog;

public class AcceptGameDialog extends DialogFragment implements ToastMessage {
	
	private static String mOpposingPlayerName;
	private static String mOpposingPlayerId;
	private static Context mApplicationContext;
	private static String mPlayerName;
	private static Integer mPlayerId;
	private static Resources mResources;
	public static ErrorHandler errorHandler;	

    public AcceptGameDialog() {
        super();
    }

    @Override
    public void setArguments(Bundle myBundle) {
        mOpposingPlayerName = myBundle.getString("opponentName");
        mOpposingPlayerId = myBundle.getString("opponentId");
        mPlayerName = myBundle.getString("playerName");
        mPlayerId = myBundle.getInt("player1Id");
    }

    public void setContext(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public void setResources(Resources resources) {
        mResources = resources;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle(mOpposingPlayerName + R.string.would_like_to_play)
            .setIcon(R.drawable.willy_shmo_small_icon)
            .setNegativeButton(android.R.string.no, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rejectGame();
                }
            })
            .setPositiveButton(android.R.string.yes,  new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    acceptGame();
                }
            })
            .create();
    }
    
    public void setOpposingPlayerName(String name) {
    	mOpposingPlayerName = name;
    }
    
    private void rejectGame() { //this is what the server side will see
        String hostName = (String)WillyShmoApplication.getConfigMap("RabbitMQIpAddress");
        String queuePrefix = (String)WillyShmoApplication.getConfigMap("RabbitMQQueuePrefix");
		//String hostName = mResources.getString(R.string.RabbitMQHostName);
//		String qName = mResources.getString(R.string.RabbitMQQueuePrefix) + "-" + "server"  + "-" + mOpposingPlayerId;
		//String qName = mResources.getString(R.string.RabbitMQQueuePrefix) + "-" + "client"  + "-" + mOpposingPlayerId;
        String qName = queuePrefix + "-" + "client"  + "-" + mOpposingPlayerId;
		
		String messageToOpponent = "noPlay," + mPlayerName + ","  + mPlayerId;
		new SendMessageToRabbitMQTask().execute(hostName, qName, null, messageToOpponent, this, mResources);
    }
    
    private void acceptGame() { //this is what the server side will see
        SharedPreferences settings = mApplicationContext.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ga_opponent_screenName", mOpposingPlayerName);
        editor.commit();  
    	
    	Intent i = new Intent(mApplicationContext, GameActivity.class);
    	i.putExtra(GameActivity.START_SERVER, "true");
        //i.putExtra(GameActivity.START_CLIENT, "true"); 
    	i.putExtra(GameActivity.PLAYER1_ID, mPlayerId); 
    	i.putExtra(GameActivity.PLAYER1_NAME, mPlayerName);
    	i.putExtra(GameActivity.START_CLIENT_OPPONENT_ID, mOpposingPlayerId);
        i.putExtra(GameActivity.PLAYER2_NAME, mOpposingPlayerName); 
        i.putExtra(GameActivity.HAVE_OPPONENT, "true"); 
        writeToLog("AcceptGameDialog", "starting server only");
        startActivity(i);        
    }

    private static void writeToLog(String filter, String msg) {
    	if ("true".equalsIgnoreCase(mResources.getString(R.string.debug))) {
    		Log.d(filter, msg);
    	}
    }
    
    public void sendToastMessage(String message) {
    	Message msg = AcceptGameDialog.errorHandler.obtainMessage();
    	msg.obj = message;
    	AcceptGameDialog.errorHandler.sendMessage(msg);	
    }
    
    public void finish() { // to fulfill contract with ToastMessage
    }
    
    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
    		Toast.makeText(mApplicationContext, (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
}
