package motocitizen.app.osm;

import java.util.ArrayList;
import java.util.List;

import motocitizen.main.R;
import motocitizen.startup.Startup;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class OSMUserOverlay extends ItemizedOverlay<OverlayItem>{
	List<OverlayItem> points= new ArrayList<OverlayItem>();

	public OSMUserOverlay(){
		this(Startup.context.getResources().getDrawable(R.drawable.osm_moto_icon),null);
	}
	
	public OSMUserOverlay(Drawable pDefaultMarker, ResourceProxy pResourceProxy) {
		super(pDefaultMarker, pResourceProxy);
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		return false;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}
}
