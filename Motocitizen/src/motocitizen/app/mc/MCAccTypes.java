package motocitizen.app.mc;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class MCAccTypes {
	private static Map<String, MCAccType> type;
	
	public static void refresh(){
		if(type == null){
			setup();
		}
		type.get("acc_b").enabled = Startup.prefs.getBoolean("mc.show.break", true);
		type.get("acc_m").enabled = Startup.prefs.getBoolean("mc.show.acc", true);
		type.get("acc_m_m").enabled = Startup.prefs.getBoolean("mc.show.acc", true);
		type.get("acc_m_a").enabled = Startup.prefs.getBoolean("mc.show.acc", true);
		type.get("acc_m_p").enabled = Startup.prefs.getBoolean("mc.show.acc", true);
		type.get("acc_o").enabled = Startup.prefs.getBoolean("mc.show.other", true);
		type.get("acc_s").enabled = Startup.prefs.getBoolean("mc.show.steal", true);
	}
	
	public static BitmapDescriptor getBitmapDescriptor(String name){
		if(type == null){
			refresh();
		}
		return BitmapDescriptorFactory.fromResource(type.get(name).resId);
	}
	public static Drawable getDrawable(Context context, String name){
		if(type == null){
			refresh();
		}
		return context.getResources().getDrawable(type.get(name).resId);
	}
	public static MCAccType get(String name){
		if(type == null){
			refresh();
		}
		return type.get(name);
	}
	
	private static void setup(){
		type = new HashMap<String, MCAccType>();
		type.put("acc_b", new MCAccType(R.drawable.break_icon, true));
		type.put("acc_m", new MCAccType(R.drawable.accident, true));
		type.put("acc_m_m", new MCAccType(R.drawable.accident, true));
		type.put("acc_m_a", new MCAccType(R.drawable.accident, true));
		type.put("acc_m_p", new MCAccType(R.drawable.accident, true));
		type.put("acc_o", new MCAccType(R.drawable.other, true));
		type.put("acc_s", new MCAccType(R.drawable.other, true));
		type.put("user", new MCAccType(R.drawable.osm_moto_icon, true));
	}
	
	public static class MCAccType{
		public boolean enabled;
		public int resId;
		public MCAccType(int id, boolean enabled){
			this.enabled = enabled;
			this.resId = id;
		}
	}
}
