package com.guzzardo.android.willyshmo.tictactoe4;

import android.content.Intent;

public interface ToastMessage {
	void sendToastMessage(String message);
	void finish();
	void startActivity(Intent i);
}
