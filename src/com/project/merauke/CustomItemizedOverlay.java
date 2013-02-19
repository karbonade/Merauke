/***
 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.project.merauke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CustomItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	private Context c;
	private MapView maps;
	
	private ProgressDialog whellProgress;
	
	// String coordinates for Jl.Dipatiukur near ITHB initial to zoom
	static String strOrigin[] = {"-6.888435", "107.615631"};
	static String HTTP_URL = "http://www.jejaringhotel.com/android/showme.php";
	// CustomItemizedOverlay<CustomOverlayItem> currItemizedOverlay = null;
	
	double maxLat = -6.913987;
	double minLat = -6.883141;
	double minLng = 107.606692;
	double maxLng = 107.617593;
	
	public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		maps = mapView;
		c = mapView.getContext();
		
		whellProgress = new ProgressDialog(c);		
		whellProgress.setIndeterminate(true);
		whellProgress.setMessage("Please wait...");
				
		new FetchDataTask() { 
			protected void onPreExecute() {
	    		whellProgress.show();
	        }
			
	        protected void onPostExecute(ArrayList<CustomOverlayItem> result) {
	            if (result != null) {
	            	storeAll(result);
	            	whellProgress.dismiss();
	            	
	            	maps.invalidate();
	            }
	        }
	    }.execute();
		// TODO
	}

	public void storeAll(ArrayList<CustomOverlayItem> listOverlays) {
		m_overlays = listOverlays;
		populate();
	}
	
	public void addOverlay(CustomOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		Toast.makeText(c, "onBalloonTap for overlay index " + index,
				Toast.LENGTH_LONG).show();
		return true;
	}

	public void removeAll(){
		m_overlays.clear();
	}
		
	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}

	
	boolean isMove = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView map) {
		
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			isMove = true;
		}
		
		if((event.getAction() == MotionEvent.ACTION_UP) && isMove) {
			Log.d("ACTION", "UP after MOVE");
			// Toast.makeText(c, "geser-geser detected", Toast.LENGTH_SHORT).show();
			Projection projection = maps.getProjection();
			GeoPoint geoPointMin = (GeoPoint) projection.fromPixels(0, 0);
		    GeoPoint geoPointMax = (GeoPoint) projection.fromPixels(maps.getWidth(), maps.getHeight());
			
		    minLat = geoPointMin.getLatitudeE6() / 1E6;
		    minLng = geoPointMin.getLongitudeE6() / 1E6;
		    // Log.d("drikvy-min", minLat+"---"+minLng);// 2-3
		    
		    maxLat = geoPointMax.getLatitudeE6() / 1E6;
		    maxLng = geoPointMax.getLongitudeE6() / 1E6;
		    // Log.d("drikvy-max", maxLat+"---"+maxLng);// 1-4
		    
		    new FetchDataTask() { 
		    	
		    	protected void onPreExecute() {
		    		whellProgress.show();
		        }
		    	
		        protected void onPostExecute(ArrayList<CustomOverlayItem> result) {
		            if (result != null) {
		            	storeAll(result);
		            	whellProgress.dismiss();
		            	
		            	maps.invalidate();
		            }
		        }
		    }.execute();
		    
		    isMove = false;
		}
		return false;
	}
	
		
	/**
	 * get inputstream data from http request
	 * */
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
	
	/**
	 * extract JSON into usable data
	 * */
	private ArrayList<CustomOverlayItem> processingJSON(String strJSON) {
		ArrayList<CustomOverlayItem> listPoints = new ArrayList<CustomOverlayItem>();
		try {
			JSONArray rowArray = new JSONArray(strJSON);
			// Log.d("JSON", "total: "+rowArray.length());
			
			int count = 0;
			JSONObject jsonElement = null;
			int posLat = 0;
			int posLng = 0;
			String name = "";
			String alamat = "";
			
			if(rowArray.length() != 0) {
				this.removeAll();// TODO robust impl.
				
				while(count < rowArray.length()) {
					
					jsonElement = rowArray.getJSONObject(count);
					posLat = (int) (Double.parseDouble
							(jsonElement.getString("lat")) * 1E6);
					posLng = (int) (Double.parseDouble
							(jsonElement.getString("lng")) * 1E6);
					name = jsonElement.getString("namalokasi");
					alamat = jsonElement.getString("alamat");
					
					listPoints.add(new CustomOverlayItem(new GeoPoint(posLat, posLng)
					, name, alamat
					, "http://ia.media-imdb.com/images/M/MV5BMTM1MTk2ODQxNV5BMl5BanBnXkFtZTcwOTY5MDg0NA@@._V1._SX40_CR0,0,40,54_.jpg"));
					
					count++;
				}
				Log.d("JSON", "loading data finish");
			} else {
				Log.d("JSON", "EMPTY");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return listPoints;
	}
	
	private class FetchDataTask extends AsyncTask<String, Integer, ArrayList<CustomOverlayItem>> {
	    @Override
	    protected ArrayList<CustomOverlayItem> doInBackground(String... arg0) {
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
	        
	        return processingJSON(strJSON);
	    }	
	}
	
}
