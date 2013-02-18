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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class DrikvyMap extends MapView {
		
		Context cx;
		
		double maxLat = -6.913987;
		double minLat = -6.883141;
		double minLng = 107.606692;
		double maxLng = 107.617593;
	
		// String coordinates for Jl.Dipatiukur near ITHB initial to zoom
		static String strOrigin[] = {"-6.888435", "107.615631"};
		static String HTTP_URL = "http://www.jejaringhotel.com/android/showme.php";
		CustomItemizedOverlay<CustomOverlayItem> currItemizedOverlay = null;
		
		public DrikvyMap(android.content.Context context, android.util.AttributeSet attrs) {
			super(context, attrs);
			// this is it!!!
			Log.d("drikvy-map", "1");
			cx = context;
			/*
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.company_32);
						
			currItemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable, this);
			this.getOverlays().add(currItemizedOverlay);
			
			Thread loadThread = new Thread(runLoadPosition);
			loadThread.start();*/
		}
		
		public DrikvyMap(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			//Log.d("drikvy-map", "2");
			cx = context;
		}
		
		public DrikvyMap(android.content.Context context, java.lang.String apiKey) {
			super(context, apiKey);
			//Log.d("drikvy-map", "3");
			cx = context;
		}
		/*
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (ev.getAction()==MotionEvent.ACTION_UP) {
				//do your thing
				Toast.makeText(cx, "geser-geser detected", Toast.LENGTH_SHORT).show();
				
				Projection projection = this.getProjection();
				GeoPoint geoPointMin = (GeoPoint) projection.fromPixels(0, 0);
			    GeoPoint geoPointMax = (GeoPoint) projection.fromPixels(this.getWidth(), this.getHeight());
				
			    minLat = geoPointMin.getLatitudeE6() / 1E6;
			    minLng = geoPointMin.getLongitudeE6() / 1E6;
			    Log.d("drikvy-min", minLat+"---"+minLng);// 2-3
			    
			    maxLat = geoPointMax.getLatitudeE6() / 1E6;
			    maxLng = geoPointMax.getLongitudeE6() / 1E6;
			    Log.d("drikvy-max", maxLat+"---"+maxLng);// 1-4
			    
				Thread loadThread = new Thread(runLoadPosition);
				loadThread.start();
			}
			
			return super.onTouchEvent(ev);
		}
		
		/*
		Runnable runLoadPosition = new Runnable() {
			
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader
						(getConnection(HTTP_URL+"?maxLat="+maxLat+"&minLat="
				+minLat+"&minLng="+minLng+"&maxLng="+maxLng)));
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
		};
		
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
				
				if(rowArray.length() != 0) {
					currItemizedOverlay.removeAll();// TODO robust impl.
				}
				
				while(count < rowArray.length()) {
					Thread.sleep(500);
					
					jsonElement = rowArray.getJSONObject(count);
					posLat = (int) (Double.parseDouble
							(jsonElement.getString("lat")) * 1E6);
					posLng = (int) (Double.parseDouble
							(jsonElement.getString("lng")) * 1E6);
					// (Double.parseDouble(jsonElement.getString("namalokasi")) * 1E6)
					// (Double.parseDouble(jsonElement.getString("alamat")) * 1E6)
					
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
				currItemizedOverlay.addOverlay(new CustomOverlayItem(posPoint
						, "default", "default balloon"
						, "http://ia.media-imdb.com/images/M/MV5BMTM1MTk2ODQxNV5BMl5BanBnXkFtZTcwOTY5MDg0NA@@._V1._SX40_CR0,0,40,54_.jpg"));
				DrikvyMap.this.invalidate();
			};
		};
		*/
		int oldZoomLevel=-1;
		@Override
		public void dispatchDraw(Canvas canvas) {
			super.dispatchDraw(canvas);
			if (getZoomLevel() != oldZoomLevel) {
				//do your thing
				Toast.makeText(cx, "zoom detected", Toast.LENGTH_SHORT).show();
				oldZoomLevel = getZoomLevel();
			}
		}
		
}