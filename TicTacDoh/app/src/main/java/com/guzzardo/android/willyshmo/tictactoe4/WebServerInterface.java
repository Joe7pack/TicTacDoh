package com.guzzardo.android.willyshmo.tictactoe4;


import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WebServerInterface { 
	
    private static ToastMessage mToastMessage;
    private static Resources mResources;
	
    public static String converseWithWebServer(String url, String urlToEncode, ToastMessage toastMessage, Resources resources) {
    	BufferedInputStream bis = null; 
    	InputStream is = null;
    	String result = null;
    	mResources = resources;
    	mToastMessage = toastMessage;
    	String errorAt = null;
    	int responseCode = 0;
    	boolean networkAvailable = false;
    	
        try {
			writeToLog("WebServerInterface", "converseWithWebServer() called");
        	URL myURL = null;
				if (urlToEncode == null) {
        		myURL = new URL(url);
        	} else {
        		String encodedUrl = URLEncoder.encode(urlToEncode, "UTF-8");
        		myURL = new URL(url+encodedUrl);
        	}
        	errorAt = "openConnection";
        	HttpURLConnection httpUrlConnection = (HttpURLConnection) myURL.openConnection();
        	httpUrlConnection.setRequestMethod("POST");
        	httpUrlConnection.setConnectTimeout(3000);
        	responseCode = httpUrlConnection.getResponseCode();        	
        	errorAt = "getInputStream";
			is = httpUrlConnection.getInputStream(); // define InputStreams to read from the URLConnection.        	
			errorAt = "bufferedInputStream";
			bis = new BufferedInputStream(is);
			errorAt = "convertStreamToString";
			result = convertStreamToString(is); // convert the Bytes read to a String.
			networkAvailable = true;
        } catch (Exception e) {
        	//e.printStackTrace();
        	writeToLog("WebServerInterface", "response code: " + responseCode + " error: " + e.getMessage() + " error at: " + errorAt);   	
        	String networkNotAvailable = resources.getString(R.string.network_not_available);
        	mToastMessage.sendToastMessage(networkNotAvailable);
        	
        } finally {
        	try {
        		bis.close();
        		is.close();
        	} catch (Exception e) {
        		//nothing to do here
        		writeToLog("WebServerInterface", "finally exception: " + e.getMessage());
        	}
        }
        
        WillyShmoApplication.setNetworkAvailable(networkAvailable);
        return result;
    }
        	
    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
//            e.printStackTrace();
        	writeToLog("WebServerInterface", "convertStreamToString IOException: " + e.getMessage());        	
        	mToastMessage.sendToastMessage(e.getMessage());        	
        } catch (Exception e) {
  //      	e.printStackTrace();
        	writeToLog("WebServerInterface", "convertStreamToString Exception: " + e.getMessage());        	
        	mToastMessage.sendToastMessage(e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
//                e.printStackTrace();
            	writeToLog("WebServerInterface", "is close IOException:: " + e.getMessage());        	
            	mToastMessage.sendToastMessage(e.getMessage());
            }
        }
        return sb.toString();
    }
	
    private static void writeToLog(String filter, String msg) {
    	if ("true".equalsIgnoreCase(mResources.getString(R.string.debug)))
    		Log.d(filter, msg);
    }
	
}
