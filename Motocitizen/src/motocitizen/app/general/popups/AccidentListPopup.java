package motocitizen.app.general.popups;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.Accident;
import motocitizen.app.general.user.Role;
import motocitizen.startup.Startup;
import motocitizen.utils.MyUtils;

public class AccidentListPopup extends PopupWindowGeneral {
    public static PopupWindow getPopupWindow(Context context,int id, boolean disableOldItems) {
        Accident p = AccidentsGeneral.points.getPoint(id);
        content = new TableLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020);
        content.setLayoutParams(lp);
        textToCopy = AccidentsGeneral.points.getTextToCopy(id);
        content.addView(copyButtonRow(), lp);

        if (!disableOldItems) {
            for (String phone : MyUtils.getPhonesFromText(p.getDescription())) {
                content.addView(phoneButtonRow(phone), lp);
                content.addView(smsButtonRow(phone), lp);
            }
        }
        if (Role.isModerator()) {
            if (!disableOldItems) {
                content.addView(finishButtonRow(p));
                content.addView(hideButtonRow(p));
            }
        }
        content.addView(shareMessage(context));
        pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        pw.setOutsideTouchable(true);
        pw.setContentView(content);
        return pw;
    }
}
