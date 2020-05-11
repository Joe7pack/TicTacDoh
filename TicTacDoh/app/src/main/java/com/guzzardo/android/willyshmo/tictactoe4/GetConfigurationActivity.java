package com.guzzardo.android.willyshmo.tictactoe4;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class GetConfigurationActivity extends Activity implements ToastMessage {

    public static GetConfigurationActivity.ErrorHandler mErrorHandler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetConfigurationValuesFromDB getConfigurationValuesFromDB = new GetConfigurationValuesFromDB();
        getConfigurationValuesFromDB.execute(this, getApplicationContext(), getResources());
    }

    public void sendToastMessage(String message) {
        Message msg = GetConfigurationActivity.mErrorHandler.obtainMessage();
        msg.obj = message;
        GetConfigurationActivity.mErrorHandler.sendMessage(msg);
    }

    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }
}
