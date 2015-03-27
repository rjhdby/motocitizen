package motocitizen.app.mc;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

import motocitizen.Activity.ConfigActivity;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class MCAccTypes {
    private final static Map<String, MCAccType> type = new HashMap<>();
    static{
        type.put("acc_b", new MCAccType(R.drawable.break_icon));
        type.put("acc_m", new MCAccType(R.drawable.accident));
        type.put("acc_m_m", new MCAccType(R.drawable.accident));
        type.put("acc_m_a", new MCAccType(R.drawable.accident));
        type.put("acc_m_p", new MCAccType(R.drawable.accident));
        type.put("acc_o", new MCAccType(R.drawable.other));
        type.put("acc_s", new MCAccType(R.drawable.other));
        type.put("user", new MCAccType(R.drawable.osm_moto_icon));
    }

    public static void refresh() {
        type.get("acc_b").enabled = ConfigActivity.isShowBreak(Startup.prefsDef);
        type.get("acc_m").enabled = ConfigActivity.isShowAcc(Startup.prefsDef);
        type.get("acc_m_m").enabled = ConfigActivity.isShowAcc(Startup.prefsDef);
        type.get("acc_m_a").enabled = ConfigActivity.isShowAcc(Startup.prefsDef);
        type.get("acc_m_p").enabled = ConfigActivity.isShowAcc(Startup.prefsDef);
        type.get("acc_o").enabled = ConfigActivity.isShowOther(Startup.prefsDef);
        type.get("acc_s").enabled = ConfigActivity.isShowSteal(Startup.prefsDef);
    }

    public static BitmapDescriptor getBitmapDescriptor(String name) {
        return BitmapDescriptorFactory.fromResource(type.get(name).resId);
    }

    public static Drawable getDrawable(Context context, String name) {
        return context.getResources().getDrawable(type.get(name).resId);
    }

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
}
