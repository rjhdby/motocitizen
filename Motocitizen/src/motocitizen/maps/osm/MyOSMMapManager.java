package motocitizen.maps.osm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;

public class MyOSMMapManager extends MyMapManager {
    private static MapView                          map;
    private static ItemizedIconOverlay<OverlayItem> userOverlay, accOverlay;
    private static Context context;

    public MyOSMMapManager(Context context) {
        MyOSMMapManager.context = context;
        setName(MyMapManager.OSM);
        LayoutInflater li     = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup      parent = (ViewGroup) ((Activity) context).findViewById(R.id.map_container);
        View           child  = li.inflate(R.layout.osm_view_content, parent, false);
        if (parent.getChildCount() > 0) parent.removeAllViews();
        parent.addView(child);
        userOverlay = OSMUserOverlay.getUserOverlay(context);
        accOverlay = OSMAccOverlay.getOverlay(context);
        Activity act = (Activity) context;
        map = (MapView) act.findViewById(R.id.osm_mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(16);

        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(act);
        JumpToLocation  jumpToLocation    = new JumpToLocation(act);

        map.getOverlays().add(myScaleBarOverlay);
        map.getOverlays().add(jumpToLocation);
        goToUser();
    }

    public void placeAccidents(Context context) {
        map.getOverlays().remove(accOverlay);
        accOverlay = OSMAccOverlay.getOverlay(context);
        map.getOverlays().add(accOverlay);
        map.invalidate();
    }

    @SuppressWarnings("UnusedParameters")
    public void placeUser(Context context) {
        map.getOverlays().remove(userOverlay);
        userOverlay = OSMUserOverlay.getUserOverlay(context);
        map.getOverlays().add(userOverlay);
        map.invalidate();
    }

    private static void goToUser() {

        Location location = MyLocationManager.getLocation();
        //if( location != null) {
        GeoPoint gp = new GeoPoint(location);
        map.getController().animateTo(gp);
        map.invalidate();
        //}
    }

    public void zoom(int zoom) {
        map.getController().setZoom(zoom);
    }

    public void jumpToPoint(Location location) {
        GeoPoint gp = new GeoPoint(location);
        map.getController().animateTo(gp);
    }

    private static class JumpToLocation extends Overlay {
        public float cx, cy, r;
        private int lastZoom;

        public JumpToLocation(Activity act) {
            super(act);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event, MapView map) {
            int[] location = new int[2];
            float x        = event.getRawX();
            map.getLocationInWindow(location);
            lastZoom = -1;
            float y = event.getRawY() - location[1];
            if (x > (cx - r) && x < (cx + r) && y > (cy - r) && y < (cy + r)) {
                goToUser();
            }
            return true;
        }

        @Override
        protected void draw(Canvas canvas, MapView arg1, boolean shadow) {
            if (map.isAnimating() || shadow) {
                return;
            }

            if (map.getZoomLevel() != lastZoom) {
                lastZoom = map.getZoomLevel();
                return;
            }

            float w = map.getWidth();
            float h = map.getHeight();

            r = (float) (Math.min(w, h) * 0.07);
            cx = (float) (w - r * 1.2);
            cy = (float) (h - r * 1.2);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) (r * 0.1));
            canvas.drawCircle(cx, cy, (float) (r * 0.7), paint);
            canvas.drawLine(cx, cy + r, cx, cy - r, paint);
            canvas.drawLine(cx - r, cy, cx + r, cy, paint);
        }
    }
}
