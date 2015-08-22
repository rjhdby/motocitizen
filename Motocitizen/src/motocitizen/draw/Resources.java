package motocitizen.draw;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import motocitizen.content.Type;
import motocitizen.main.R;

public class Resources {
    public static BitmapDescriptor getMapBitmapDescriptor(Type type) {
        int id;
        switch (type) {
            case BREAK:
                id = R.drawable.break_icon;
                break;
            case SOLO:
            case MOTO_AUTO:
            case MOTO_MOTO:
            case MOTO_MAN:
                id = R.drawable.accident;
                break;
            case USER:
                id = R.drawable.osm_moto_icon;
                break;
            case STEAL:
            case OTHER:
            default:
                id = R.drawable.other;
                break;
        }
        return BitmapDescriptorFactory.fromResource(id);
    }

    public static int[] getAccidentRowSetCommon(){
        return new int[]{R.layout.accident_row, R.drawable.accident_row_ended, R.drawable.accident_row_hidden};
    }
    public static int[] getAccidentRowSetOwner(){
        return new int[]{R.layout.accident_row_i_was_here, R.drawable.owner_accident_ended, R.drawable.owner_accident_hidden};
    }

    public static int getMessageRowDrawableIdOwner(){
        return R.layout.owner_message_row;
    }
    public static int getMessageRowDrawableIdCommnon(){
        return R.layout.message_row;
    }
}

