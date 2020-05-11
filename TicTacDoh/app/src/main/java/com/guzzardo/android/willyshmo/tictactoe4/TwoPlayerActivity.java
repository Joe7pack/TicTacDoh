package com.guzzardo.android.willyshmo.tictactoe4;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

//import android.support.v7.app.AlertDialog;

public class TwoPlayerActivity extends Activity {
	
    private String mPlayer1Name = "Player 1";
    private String mPlayer2Name = "Player 2";
    private Button mButtonPlayer1, mButtonPlayer2, mButtonPlayOverNetwork;
    public static ErrorHandler errorHandler;    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_player);
        
        String player1Name = getIntent().getStringExtra(GameActivity.PLAYER1_NAME);
        if (player1Name != null)
        	mPlayer1Name = player1Name;
        
        String player2Name = getIntent().getStringExtra(GameActivity.PLAYER2_NAME);
        if (player2Name != null)
        	mPlayer2Name = player2Name;
        
        mButtonPlayer1 = (Button) findViewById(R.id.player_1);
        mButtonPlayer1.setText(mPlayer1Name+" moves first?");
        mButtonPlayer2 = (Button) findViewById(R.id.player_2);
        mButtonPlayer2.setText(mPlayer2Name+" moves first?");

        findViewById(R.id.player_1).setOnClickListener(
                new OnClickListener() {
            public void onClick(View v) {
            	startGame(1);
//                finish();
            }
        });
        
        findViewById(R.id.player_2).setOnClickListener(
                new OnClickListener() {
            public void onClick(View v) {
            	startGame(2);
  //              finish();
            }
        });
        
        mButtonPlayOverNetwork = (Button)findViewById(R.id.play_over_network);
        mButtonPlayOverNetwork.setOnClickListener(
                new OnClickListener() {
            public void onClick(View v) {
            	playOverNetwork();
            }
        });
        
		if (WillyShmoApplication.isNetworkAvailable()) {
			mButtonPlayOverNetwork.setVisibility(View.VISIBLE);
		} else {
			mButtonPlayOverNetwork.setVisibility(View.GONE);
		}
        
    }

    private void startGame(int player) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.START_PLAYER_HUMAN,
                player == 1 ? GameView.State.PLAYER1.getValue() : GameView.State.PLAYER2.getValue());
        i.putExtra(GameActivity.PLAYER1_NAME, mPlayer1Name); 
        i.putExtra(GameActivity.PLAYER2_NAME, mPlayer2Name);         
        startActivity(i);
    }
    
    private void playOverNetwork() {
        if (mPlayer1Name.equals("") || (mPlayer1Name.equalsIgnoreCase("Player 1"))) {
        	displayNameRequiredAlert();
        	return;
        }
    	
        Intent i = new Intent(this, PlayOverNetwork.class);
        i.putExtra(GameActivity.PLAYER1_NAME, mPlayer1Name); 
        startActivity(i);   	
    }
    
    
    private void displayNameRequiredAlert() {
        try {
        	new AlertDialog.Builder(TwoPlayerActivity.this)
        	.setTitle("Please enter your name in the settings menu")

        	.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        		@Override
        			public void onClick(DialogInterface dialog, int which) {
        			TwoPlayerActivity.this.finish(); 
        			}
        		}
        	)
        	.setIcon(R.drawable.willy_shmo_small_icon)
        	.show();
        } catch (Exception e) {
        	sendToastMessage(e.getMessage());
        }
    }
    
    protected void sendToastMessage(String message) {
    	Message msg = TwoPlayerActivity.errorHandler.obtainMessage();
    	msg.obj = message;
    	TwoPlayerActivity.errorHandler.sendMessage(msg);	
    }
    
    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
    		Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();    
//        String player1Name = getIntent().getStringExtra(GameActivity.PLAYER1_NAME);
//        if (player1Name != null)
//        	mPlayer1Name = player1Name;
//        
//        String player2Name = getIntent().getStringExtra(GameActivity.PLAYER2_NAME);
//        if (player2Name != null)
//        	mPlayer2Name = player2Name;
    }    
    
}
