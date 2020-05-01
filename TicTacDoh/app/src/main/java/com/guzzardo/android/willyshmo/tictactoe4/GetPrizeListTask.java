package com.guzzardo.android.willyshmo.tictactoe4;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import static com.guzzardo.android.willyshmo.tictactoe4.WillyShmoApplication.getWillyShmoApplicationContext;

/**
 * An AsyncTask that will be used to get a list of available prizes
 */
public class GetPrizeListTask extends AsyncTask<Object, Void, String> {

	private ToastMessage mCallerActivity;
	private Context applicationContext;
	private static Resources mResources;
    private static String [] mPrizeImages;
    private static String [] mPrizeImageWidths;
    private static String [] mPrizeImageHeights;
    
    private static String [] mPrizeNames;
    private static String [] mPrizeUrls;
    private static String [] mPrizeLocations;  
    private static String [] mPrizeIds;
    private static String [] mPrizeDistances;
    private static Bitmap [] mBitmapImages;
    private static boolean mStartMainActivity;
    
	@Override
	protected String doInBackground(Object... params) {
		
		String prizesAvailable = null;
    	mCallerActivity = (ToastMessage)params[0];
//    	applicationContext = (Context)params[1]; 
    	mResources = (Resources)params[1];
    	mStartMainActivity = Boolean.valueOf((String)params[2]);
    	
    	double longitude = WillyShmoApplication.getLongitude();
    	double latitude = WillyShmoApplication.getLatitude();
    	
    	String url = mResources.getString(R.string.domainName) + "/prize/getPrizesByDistance/?longitude=" + longitude + "&latitude=" + latitude;
    	
		try {
			prizesAvailable = WebServerInterface.converseWithWebServer(url, null, mCallerActivity, mResources);
		} catch (Exception e) { 
//			System.out.println(e.getMessage());
			writeToLog("GetPrizeListTask", "doInBackground: " + e.getMessage());			
			mCallerActivity.sendToastMessage("Playing without host server");			
		}
		writeToLog("GetPrizeListTask", "WebServerInterfaceUsersOnlineTask doInBackground called usersOnline: " + prizesAvailable);  		
		return prizesAvailable;
	}	
	
	protected void onPostExecute(String prizesAvailable) {
		try {
			writeToLog("GetPrizeListTask", "onPostExecute called usersOnline: " + prizesAvailable);
			
			if (mStartMainActivity) {
				//mCallerActivity.startActivity(new Intent("MainActivity"));

                Context appContext = WillyShmoApplication.getWillyShmoApplicationContext();

				//Intent myIntent = new Intent();
				//myIntent.setClass()
				//mCallerActivity.startActivity(myIntent);
				Context willyShmoApplicationContext = getWillyShmoApplicationContext();
				Intent myIntent = new Intent(willyShmoApplicationContext, MainActivity.class);
				mCallerActivity.startActivity(myIntent);

				//mCallerActivity.startActivity("MainActivity");



				mCallerActivity.finish();
			}
			
			if (prizesAvailable != null && prizesAvailable.length() > 20) {
				getPrizesAvailable(prizesAvailable);
				convertStringsToBitmaps();
				savePrizeArrays();
			} else {
				WillyShmoApplication.setPrizeNames(null);
			}
//			if (mStartMainActivity) {
//				mCallerActivity.finish();
//				mCallerActivity.startActivity(new Intent("MainActivity"));
//			}
		} catch (Exception e) {
//			System.out.println(e.getMessage());
			writeToLog("GetPrizeListTask", "onPostExecute exception called " + e.getMessage());
			mCallerActivity.sendToastMessage(e.getMessage());			
    	}
	}
	
    private static void writeToLog(String filter, String msg) {
    	if ("true".equalsIgnoreCase(mResources.getString(R.string.debug))) {
    		Log.d(filter, msg);
    	}
    }
	
    private void getPrizesAvailable(String prizesAvailable) {
    	TreeMap<String, String[]> prizes = parsePrizeList(prizesAvailable);

    	Set<String> userKeySet = prizes.keySet(); // this is where the keys (userNames) gets sorted
    	Iterator<String> keySetIterator = userKeySet.iterator();

    	Object[] objectArray = prizes.keySet().toArray();
    	mPrizeNames = new String[objectArray.length];
    	mPrizeIds = new String[objectArray.length];
    	mPrizeImages = new String[objectArray.length];
    	mPrizeImageWidths = new String[objectArray.length];
    	mPrizeImageHeights = new String[objectArray.length];
    	mPrizeDistances = new String[objectArray.length];
		mBitmapImages = new Bitmap[objectArray.length];
    	mPrizeUrls = new String[objectArray.length];
    	mPrizeLocations = new String[objectArray.length];
    	
    	for (int x = 0; x < objectArray.length; x++) {
    		mPrizeNames[x] = (String)objectArray[x];
            String[] prizeValues = prizes.get(objectArray[x]);
            mPrizeIds[x] =  prizeValues[0];
            StringBuilder workString = new StringBuilder(prizeValues[1]);
            String newImage = workString.substring(1, workString.length()-1);
            mPrizeImages[x] = newImage; 
            mPrizeImageWidths[x] =   prizeValues[2];   
            mPrizeImageHeights[x] =   prizeValues[3]; 
            mPrizeDistances[x] = prizeValues[4];
            mPrizeUrls[x] = prizeValues[5];  
            mPrizeLocations[x] = prizeValues[6];
    	}
    }
    
    private TreeMap<String, String[]> parsePrizeList(String prizesAvailable) {
    	TreeMap<String, String[]> userTreeMap = new TreeMap<String, String[]>();
    	
    	try {
    		String convertedPrizesAvailable = convertToArray(new StringBuilder(prizesAvailable));
    		//String endOfString = convertedPrizesAvailable.substring(convertedPrizesAvailable.length() - 60 );
    		//System.out.println("end of string: " + endOfString );
    	
    		JSONObject jsonObject = new JSONObject(convertedPrizesAvailable);
    		JSONArray prizeArray = jsonObject.getJSONArray("PrizeList"); 
    		for ( int x = 0; x < prizeArray.length(); x++) {
    			JSONObject prize = prizeArray.getJSONObject(x);
    			int prizeId = prize.getInt("id");
    			double distance = prize.getDouble("distance");
    			String prizeName = prize.getString("name");
    			String image = prize.getString("image");
    			String prizeUrl = prize.getString("url");
    			
    			String location = prize.getString("location");
    			
    			int imageWidth = prize.getInt("imageWidth");
    			int imageHeight = prize.getInt("imageHeight");
        		String [] prizeArrayValues = new String[7];
        		prizeArrayValues[0] = Integer.toString(prizeId);
        		prizeArrayValues[1] = image;
        		prizeArrayValues[2] = Integer.toString(imageWidth);
        		prizeArrayValues[3] = Integer.toString(imageHeight);
        		prizeArrayValues[4] = Double.toString(distance);
        		prizeArrayValues[5] = prizeUrl;
        		prizeArrayValues[6] = location;
        		userTreeMap.put(prizeName, prizeArrayValues);  
    		}
        	
        } catch (JSONException e) {
        	writeToLog("GetPrizeListTask", "PrizeList: " + e.getMessage());
			mCallerActivity.sendToastMessage(e.getMessage());			
        }
        return userTreeMap;
    }
    
    private String convertToArray(StringBuilder inputString) {
    	int startValue = 0;
    	int start = 0;
    	int end = 0;
    	String replaceString = "\"prize:";

		start = inputString.indexOf(replaceString,startValue);
		end = inputString.indexOf("{", start+1);
		inputString = inputString.replace(start-1, end, "[");
		startValue = end;
		
//    	for (int x = end; x < inputString.length(); x += startValue) {
    	for (int x = end; x < inputString.length(); x++) {
		
    		start = inputString.indexOf(replaceString,startValue);
    		if (start > -1) {
    			end = inputString.indexOf("{", start);
    			inputString = inputString.replace(start, end, "");
    			startValue = end;
    			//System.out.println("replaced value: " + inputString.substring(start-10, end+10));
    		} else {
    			break;
    		}
    	}    	
    	end = inputString.length()-5;
    	
    	start = inputString.indexOf("}}}",end);
    	inputString = inputString.replace(start, inputString.length()-1, "}]}");
    	return inputString.toString();
    }
    
	private void convertStringsToBitmaps() {
		for (int x=0; x < mPrizeIds.length; x++) {
			String [] imageStrings = mPrizeImages[x].split(",");
			byte[] imageBytes = new byte[imageStrings.length];
			for (int y=0; y<imageBytes.length; y++) {
				imageBytes[y] = Byte.parseByte(imageStrings[y]);
			}
			mBitmapImages[x] = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    	}
    }
	
	private void savePrizeArrays() {
		WillyShmoApplication.setPrizeIds(mPrizeIds);
//		WillyShmoApplication.setPrizeImages(mPrizeImages);
		WillyShmoApplication.setPrizeNames(mPrizeNames);
		WillyShmoApplication.setBitmapImages(mBitmapImages);
		WillyShmoApplication.setImageWidths(mPrizeImageWidths);
		WillyShmoApplication.setImageHeights(mPrizeImageHeights);
		WillyShmoApplication.setPrizeDistances(mPrizeDistances);
		WillyShmoApplication.setPrizeUrls(mPrizeUrls);
		WillyShmoApplication.setPrizeLocations(mPrizeLocations);
	}
    
}




