package com.guzzardo.android.willyshmo.tictactoe4;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;

/**
 * An AsyncTask that will be used to load Configuration values from the DB
 */
public class GetConfigurationValuesFromDB extends AsyncTask<Object, Void, String> {

    //private GetConfigurationActivity mCallerActivity;
    private SplashScreen mCallerActivity;
    private Context applicationContext;
    private static Resources mResources;

    @Override
    protected String doInBackground(Object... params) {
        String configValues = null;
        //mCallerActivity = (GetConfigurationActivity)params[0];
        mCallerActivity = (SplashScreen) params[0];
        applicationContext = (Context)params[1];
        mResources = (Resources)params[2];
        String url = mResources.getString(R.string.domainName) + "/config/getConfigValues";

        try {
            configValues = WebServerInterface.converseWithWebServer(url, null, mCallerActivity, mResources);

        } catch (Exception e) {
            writeToLog("GetConfigurationValuesFromDB", "doInBackground: " + e.getMessage());
            mCallerActivity.sendToastMessage(e.getMessage());
        }
        writeToLog("GetConfigurationValuesFromDB", "GetConfigurationValuesFromDB doInBackground called usersOnline: " + configValues);
        return configValues;
    }

    protected void onPostExecute(String configValues) {
        try {
            writeToLog("GetConfigurationValuesFromDB", "onPostExecute called configValues: " + configValues);
            ObjectMapper objectMapper = new ObjectMapper();
            List<HashMap<String,Object>> result = objectMapper.readValue(configValues, List.class);

            for (int x = 0; x < result.size(); x++) {
                HashMap myHashMap = result.get(x);
                String key = (String)myHashMap.get("key");
                String value = (String)myHashMap.get("value");
                WillyShmoApplication.setConfigMap(key, value);
            }

            mCallerActivity.setAsyncMessage();

        } catch (Exception e) {
            e.printStackTrace();
            writeToLog("GetConfigurationValuesFromDB", "onPostExecute exception called " + e.getMessage());
            mCallerActivity.sendToastMessage(e.getMessage());
        }
    }

    private static void writeToLog(String filter, String msg) {
        if ("true".equalsIgnoreCase(mResources.getString(R.string.debug))) {
            Log.d(filter, msg);
        }
    }
}




