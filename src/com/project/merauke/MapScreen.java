package com.project.merauke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;

public class MapScreen extends MapActivity {

	DrikvyMap map;
	
	MyItemizedOverlay userItem = null;
	int lat = 0;
	int lng = 0;
		
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_project);
		
		map = (DrikvyMap) findViewById(R.id.mv_screen);
		map.setBuiltInZoomControls(true);
		
		// lat 'n long for initial map position
		lat = (int) (Double.parseDouble(DrikvyMap.strOrigin[0]) * 1E6);
		lng = (int) (Double.parseDouble(DrikvyMap.strOrigin[1]) * 1E6);
		GeoPoint myPoint = new GeoPoint(lat, lng);
		
		MapController mc = map.getController();
		mc.animateTo(myPoint);
	    mc.setZoom(16);
		map.invalidate();
		
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.user_arrow);
		
		// Itemized Overlay for position arrow
		userItem = new MyItemizedOverlay(drawable,
				this);
		userItem.addOverlay(new OverlayItem(myPoint, "default", null));	
		map.getOverlays().add(userItem);
		
		/*
		new Thread() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(getConnection(HTTP_URL)));
		        String line;
		        try {
					while ((line = reader.readLine()) != null) {
					  builder.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		        String strJSON = builder.toString();
		        processingJSON(strJSON);
			}
		}.start();
		*/
	}
	
	
/*
	private InputStream getConnection(String url) {
		InputStream is = null;
	
		HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
	    try {
	      HttpResponse response = client.execute(httpGet);
	      StatusLine statusLine = response.getStatusLine();
	      int statusCode = statusLine.getStatusCode();
	      if (statusCode == 200) {
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        
	      } else {
	        Log.e("ANGKOT", "Failed to download file");
	      }
	    } catch (ClientProtocolException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return is;		
	}
	
	private void processingJSON(String strJSON) {
		try {
			JSONArray rowArray = new JSONArray(strJSON);
			Log.d("JSON", "total: "+rowArray.length());
			
			int count = 0;
			JSONObject jsonElement = null;
			int posLat = 0;
			int posLng = 0;
			
			while(count < rowArray.length()) {
				Thread.sleep(500);
				
				jsonElement = rowArray.getJSONObject(count);
				posLat = (int) (Double.parseDouble
						(jsonElement.getString("lat")) * 1E6);
				posLng = (int) (Double.parseDouble
						(jsonElement.getString("lng")) * 1E6);
				
				Message posMsg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("LAT", posLat);
				bundle.putInt("LNG", posLng);
				posMsg.setData(bundle);
				
				mHandler.sendMessage(posMsg);
				Log.d("JSON", "message sent, pos: "+count);
				
				count++;
			}
			Log.d("JSON", "loading data finish");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	Handler mHandler = new Handler() {
		GeoPoint posPoint = null;
		
		public void handleMessage(android.os.Message msg) {
			
			posPoint = new GeoPoint(msg.getData().getInt("LAT")
					, msg.getData().getInt("LNG"));
			currItemizedOverlay.addOverlay(new OverlayItem(posPoint, "default", null));
			map.invalidate();
		};
	};
	*/
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
		
}
