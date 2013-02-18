package com.project.merauke;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapScreen extends MapActivity {

	MapView map;
	
	// String coordinates for Jl.Dipatiukur near ITHB initial to zoom
	static String strOrigin[] = {"-6.888435", "107.615631"};
	static String HTTP_URL = "http://www.jejaringhotel.com/android/showme.php";
	CustomItemizedOverlay<CustomOverlayItem> currItemizedOverlay = null;
	
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
		
		map = (MapView) findViewById(R.id.mv_screen);
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
		
		Drawable drawable2 = this.getResources().getDrawable(
				R.drawable.company_32);
					
		currItemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable2, map);
		map.getOverlays().add(currItemizedOverlay);
		
	}
	
	
	
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
