package com.project.merauke;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	GeoPoint geoStart = null; // origin GeoPoint
	GeoPoint geoFinish = null; // destination GeoPoint
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
	super(boundCenterBottom(defaultMarker));
	mContext = context;
	}
	 
	public void addOverlay(OverlayItem overlay) {
	mOverlays.add(overlay);
	populate();
	}
	 
	@Override
	protected OverlayItem createItem(int i) {
	return mOverlays.get(i);
	}
	 
	@Override
	public int size() {
	return mOverlays.size();
	}
	 
	public void removeAll(){
		mOverlays.clear();
	}
	
	public void removeLast(){
	mOverlays.remove(mOverlays.size()-1);
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView map) {/*
		if(geoStart == null) {
			geoStart = p;
			OverlayItem overlayitem = new OverlayItem(geoStart, "Start", "");
				// adding item to start
			this.addOverlay(overlayitem);
			 Log.d("DEBUG", "add start GeoPoints");
		} else if(geoFinish == null) {
			geoFinish = p;
			OverlayItem overlayitem = new OverlayItem(geoFinish, "Finish", "");
				// adding item to start
			this.addOverlay(overlayitem);
			 Log.d("DEBUG", "add finish GeoPoints");
		}*/
	return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView map) {
		if(event.getAction() == 1) {
			/*
			geoStart = map.getProjection().fromPixels(
					(int) event.getX(),
					(int) event.getY());
			OverlayItem overlayitem = new OverlayItem(geoStart, "Start", "");
				// adding item to start
			this.addOverlay(overlayitem);
			 Log.d("DEBUG", "add origin GeoPoints");
			 */
		}
		return false;
	}
	
}