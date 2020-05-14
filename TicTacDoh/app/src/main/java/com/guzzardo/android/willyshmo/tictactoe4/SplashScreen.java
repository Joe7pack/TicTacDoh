package com.guzzardo.android.willyshmo.tictactoe4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;

public class SplashScreen extends Activity implements ToastMessage {
    protected boolean mActive = true;
    private static boolean mSkipWaitCheck;
    private static int mSplashTime = 2500;
    public static ErrorHandler mErrorHandler;
    private static Resources mResources;

    private ProgressBar pgsBar;
    private int i = 0;
    private TextView txtView;
    public Handler threadHandler  = new Handler();
    private int progressIndex = 0;

    HandlerThread handlerThread;
    private Looper looper;
    private Handler looperHandler;
    final int SOMETHING_ACTION = 0;
    final int SOMETHING_ELSE_ACTION = 1;
    String MSG_KEY = "message key";


    /**
     * perform the action in `handleMessage` when the thread calls
     * `mHandler.sendMessage(msg)`
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString(MSG_KEY);
            final TextView myTextView = (TextView)findViewById(R.id.textView);
            myTextView.setText(string);
        }
    };

    private final Runnable mMessageSender = new Runnable() {
        public void run() {
            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(MSG_KEY, getCurrentTime());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    };

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US);
        return dateFormat.format(new Date());
    }

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

       // handlerThread.start();

        WillyShmoApplication.setWillyShmoApplicationContext(this.getApplicationContext());

        if (mPrizesAvailable) {
            //new LoadPrizesTask().execute(SplashScreen.this, getApplicationContext(), getResources());
            //mSkipWaitCheck = true;
        }

//        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound_file_1);
//        mp.start();

        pgsBar = (ProgressBar) findViewById(R.id.pBar);

        Button btn = (Button)findViewById(R.id.btnShow);
        txtView = (TextView) findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // handleButtonClick(v);
            }
        });

        startMyThread();
    }

    public void startMyThread() {
                progressIndex = pgsBar.getProgress();
                handlerThread = new HandlerThread("MyHandlerThread");
                handlerThread.start();
                looper = handlerThread.getLooper();
                looperHandler = new Handler(looper)  {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case SOMETHING_ACTION: {
                                doSomething();
                                break;
                            }
                            case SOMETHING_ELSE_ACTION:
                                doMoreThings();
                                break;
                            default:
                                break;
                        }
                    }
                };
            }

    public void setAsyncMessage() {
        Message msg = looperHandler.obtainMessage(SOMETHING_ACTION);
        looperHandler.sendMessage(msg);
    }

    public void setAsyncMessage2() {
        Message msg = looperHandler.obtainMessage(SOMETHING_ELSE_ACTION);
        looperHandler.sendMessage(msg);
    }

    private void  doSomething() {
        System.out.println("did something");
        //handlerThread.quit();
}

    private void doMoreThings() {
        System.out.println("did more things");
    }


    @Override
    public void onStart() {
        super.onStart();
        GetConfigurationValuesFromDB getConfigurationValuesFromDB = new GetConfigurationValuesFromDB();
        getConfigurationValuesFromDB.execute(this, getApplicationContext(), getResources());
        new LoadPrizesTask().execute(SplashScreen.this, getApplicationContext(), getResources());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
    }

    @Override
    public void onStop() {
        super.onStop();
        setSplashActive(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }

}