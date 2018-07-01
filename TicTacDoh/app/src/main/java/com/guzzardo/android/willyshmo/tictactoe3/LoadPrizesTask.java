package com.guzzardo.android.willyshmo.tictactoe3;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationClient;


/**
 * An AsyncTask that will be used to find other players currently online
 */
public class LoadPrizesTask extends AsyncTask<Object, Void, Integer> implements ConnectionCallbacks,
	OnConnectionFailedListener, LocationListener {

	private SplashScreen mCallerActivity;
	private Context mApplicationContext;
	private static Resources mResources;
    private static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Global variable to hold the current location
    Location mCurrentLocation;
    //LocationClient mLocationClient;
    String mPlayErrorMessage;

	@Override
	protected Integer doInBackground(Object... params) {
    	mCallerActivity = (SplashScreen)params[0];
    	mApplicationContext = (Context)params[1]; 
    	mResources = (Resources)params[2];  
    	WillyShmoApplication.setCallerActivity(mCallerActivity);


        mGoogleApiClient = new GoogleApiClient.Builder(mApplicationContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    	//mLocationClient = new LocationClient(mApplicationContext, this, this);
	    //int isPlayAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mCallerActivity);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isPlayAvailable = api.isGooglePlayServicesAvailable(mApplicationContext);

//	    mPlayErrorMessage = "test message";
	    
	    switch (isPlayAvailable) {
	    	case ConnectionResult.DEVELOPER_ERROR:
	    		mPlayErrorMessage = "The application is misconfigured.";
	    		break;
	    	case ConnectionResult.INTERNAL_ERROR:
	    		mPlayErrorMessage = "Internal Error";
	    		break;
	    	case ConnectionResult.INVALID_ACCOUNT:
	    		mPlayErrorMessage = "Your Google Play account is invalid.";
	    		break;
	    	case ConnectionResult.LICENSE_CHECK_FAILED:
	    		mPlayErrorMessage = "Google Play is not licensed to this user.";
	    		break;
	    	case ConnectionResult.NETWORK_ERROR:
	    		mPlayErrorMessage = "A network error has occurred. Please try again later.";
	    		break;
	    	case ConnectionResult.RESOLUTION_REQUIRED:
	    		mPlayErrorMessage = "Completing the connection requires some form of resolution.";
	    		break;
	    	case ConnectionResult.SERVICE_DISABLED:
	    		mPlayErrorMessage = "The installed version of Google Play services has been disabled on this device.";
	    		break;
	    	case ConnectionResult.SERVICE_INVALID:
	    		mPlayErrorMessage = "The version of the Google Play services installed on this device is not authentic.";
	    		break;
	    	case ConnectionResult.SERVICE_MISSING:
	    		mPlayErrorMessage = "Google Play services is missing on this device.";
	    		break;
	    	case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
	    		mPlayErrorMessage = "Please update Google Play Services";
	    		break;
	    	case ConnectionResult.SIGN_IN_REQUIRED:
	    		mPlayErrorMessage = "Please sign in to Google Play";
	    		break;
	    }
	    
	    if (mPlayErrorMessage != null) {
	    	return Integer.valueOf(isPlayAvailable);
	    }
	    
	    if (isPlayAvailable == ConnectionResult.SUCCESS) {
	    	try {
                writeToLog("LoadPrizesTask", "isPlayAvailable successful!");
                mGoogleApiClient.connect();
	    		//mLocationClient.connect();
                WillyShmoApplication.setGoogleApiClient(mGoogleApiClient);
                //WillyShmoApplication.getLocationListener().connect();
	    		//WillyShmoApplication.getLocationClient(true).connect();
                WillyShmoApplication.setMainStarted(true);
	    	} catch (Exception e) {
	    		mCallerActivity.sendToastMessage(e.getMessage());
	    	}
	    }
	    return Integer.valueOf(0);
	}	
	
	protected void onPostExecute(Integer isPlayAvailable) {
		try {
			if (Integer.valueOf(isPlayAvailable) != ConnectionResult.SUCCESS) {
				mCallerActivity.showGooglePlayError(isPlayAvailable, mPlayErrorMessage);
				writeToLog("LoadPrizesTask", "onPostExecute called play error: " + mPlayErrorMessage);
			} else {
	    		mCallerActivity.startWaitForPrizesPopup();
				writeToLog("LoadPrizesTask", "onPostExecute called, waiting for prizes to load from server");
			}
		} catch(Exception e) {
			writeToLog("LoadPrizesTask", "onPostExecute exception called " + e.getMessage());
			mCallerActivity.sendToastMessage(e.getMessage());			
    	}
	}
	
    private static void writeToLog(String filter, String msg) {
    	if ("true".equalsIgnoreCase(mResources.getString(R.string.debug))) {
    		Log.d(filter, msg);
    	}
    }
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
    	// mCallerActivity.sendToastMessage("Connected to Google Play");
        // Register the listener with the Location Manager to receive location updates
    	try {
    		LocationManager locationManager = (LocationManager) mCallerActivity.getSystemService(Context.LOCATION_SERVICE);
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, WillyShmoApplication.getLocationListener());
//    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    	} catch (SecurityException e) {
    		writeToLog("LoadPrizesTask", "onConnected error: " + e.getMessage());
			mCallerActivity.sendToastMessage(e.getMessage());
    	}
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    //@Override
   // public void onDisconnected() {
    //	mCallerActivity.sendToastMessage("Disconnected from Google Play. Please re-connect.");
    //}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection has been interrupted.
		// Disable any UI components that depend on Google APIs
		// until onConnected() is called.
	}
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(mCallerActivity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                //e.printStackTrace();
            	mCallerActivity.sendToastMessage("onConnectionFailed exception: " + e.getMessage());            	
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        	//FIXME - add showErrorDialog
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }  
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	writeToLog("LoadPrizesTask", "onActivityResult called");
    }

    @Override
     public void onStatusChanged(String status, int value, Bundle bundle) {
        writeToLog("LoadPrizesTask", "onStatusChanged called status: " + status);
    }

    @Override
    public void onProviderDisabled(String status) {
        writeToLog("LoadPrizesTask", "onProviderDisabled called status: " + status);
    }

    @Override
    public void onProviderEnabled(String status) {
        writeToLog("LoadPrizesTask", "onProviderEnabled called status: " + status);
    }

    @Override
    public void onLocationChanged(Location location) {
        writeToLog("LoadPrizesTask", "onLocationChanged called, new location latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
    }



}




