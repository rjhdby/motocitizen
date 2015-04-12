package motocitizen.app.mc;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;

public class MCAccTypes {
    private final static Map<String, Integer> type = new HashMap<>();

    static {
        type.put("acc_b", R.drawable.break_icon);
        type.put("acc_m", R.drawable.accident);
        type.put("acc_m_m", R.drawable.accident);
        type.put("acc_m_a", R.drawable.accident);
        type.put("acc_m_p", R.drawable.accident);
        type.put("acc_o", R.drawable.other);
        type.put("acc_s", R.drawable.other);
        type.put("user", R.drawable.osm_moto_icon);
    }

    /*
        public static void refresh() {
            type.get("acc_b").enabled = Configuration.isShowBreak(Startup.prefs);
            type.get("acc_m").enabled = Configuration.isShowAcc(Startup.prefs);
            type.get("acc_m_m").enabled = Configuration.isShowAcc(Startup.prefs);
            type.get("acc_m_a").enabled = Configuration.isShowAcc(Startup.prefs);
            type.get("acc_m_p").enabled = Configuration.isShowAcc(Startup.prefs);
            type.get("acc_o").enabled = Configuration.isShowOther(Startup.prefs);
            type.get("acc_s").enabled = Configuration.isShowSteal(Startup.prefs);
        }
    */
    public static BitmapDescriptor getBitmapDescriptor(String name) {
        return BitmapDescriptorFactory.fromResource(type.get(name));
    }

    public static Drawable getDrawable(Context context, String name) {
        return context.getResources().getDrawable(type.get(name));
    }
/*
    public static MCAccType get(String name) {
        return type.get(name);
    }

    public static class MCAccType {
        public boolean enabled;
        public final int resId;

        public MCAccType(int id) {
            this.enabled = true;
            this.resId = id;
        }
    }
*/
}
