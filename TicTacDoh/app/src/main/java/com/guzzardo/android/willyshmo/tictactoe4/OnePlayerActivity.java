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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.guzzardo.android.willyshmo.tictactoe4.GameView.State;



public class OnePlayerActivity extends Activity {
	
    private String mPlayer1Name = "Player 1";
    private String mPlayer2Name = "Willy";
    private Button mButtonPlayer1MoveFirst, mButtonPlayer2MoveFirst;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_player);
        
        String player1Name = getIntent().getStringExtra(GameActivity.PLAYER1_NAME);
        if (player1Name != null)
        	mPlayer1Name = player1Name;
        
//        String player2Name = getIntent().getStringExtra(GameActivity.PLAYER2_NAME);
//        if (player2Name != null)
//        	mPlayer2Name = player2Name;
        
        mButtonPlayer1MoveFirst = (Button) findViewById(R.id.start_player);
        mButtonPlayer1MoveFirst.setText(mPlayer1Name+" moves first?");
        mButtonPlayer2MoveFirst = (Button) findViewById(R.id.start_comp);
        mButtonPlayer2MoveFirst.setText(mPlayer2Name+" moves first?");

//        findViewById(R.id.player_1).setOnClickListener(
//                new OnClickListener() {
//            public void onClick(View v) {
//            	startGame(1);
//                finish();
//            }
//        });
//        
//        findViewById(R.id.player_2).setOnClickListener(
//                new OnClickListener() {
//            public void onClick(View v) {
//            	startGame(2);
//                finish();
//            }
//        });

    findViewById(R.id.start_player).setOnClickListener(
            new OnClickListener() {
        public void onClick(View v) {
            startGame(true);
        }
    });

    findViewById(R.id.start_comp).setOnClickListener(
            new OnClickListener() {
        public void onClick(View v) {
            startGame(false);
        }
    });     
    
    }
    
    private void startGame(boolean startWithHuman) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_START_PLAYER,
                startWithHuman ? State.PLAYER1.getValue() : State.PLAYER2.getValue());
        i.putExtra(GameActivity.PLAYER1_NAME, mPlayer1Name); 
        i.putExtra(GameActivity.PLAYER2_NAME, mPlayer2Name); 
        startActivity(i);
    }    
    
    @Override
    protected void onResume() {
        super.onResume();    
    }    
    
}
