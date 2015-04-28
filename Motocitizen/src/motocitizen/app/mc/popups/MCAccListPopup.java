package motocitizen.app.mc.popups;

import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.user.MCRole;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.MCUtils;

public class MCAccListPopup extends MCPopupWindow {
    public static PopupWindow getPopupWindow(int id, boolean disableOldItems) {
        MCPoint p = MCAccidents.points.getPoint(id);
        content = new TableLayout(Startup.context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020);
        content.setLayoutParams(lp);
        textToCopy = MCAccidents.points.getTextToCopy(id);
        content.addView(copyButtonRow(), lp);

        if (!disableOldItems) {
            for (String phone : MCUtils.getPhonesFromText(p.getDescription())) {
                content.addView(phoneButtonRow(phone), lp);
                content.addView(smsButtonRow(phone), lp);
            }
        }
        if (MCRole.isModerator()) {
            if (!disableOldItems) {
                content.addView(finishButtonRow(p));
                content.addView(hideButtonRow(p));
            }
        }
        content.addView(shareMessage(Startup.context));
        pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        pw.setOutsideTouchable(true);
        pw.setContentView(content);
        return pw;
    }
}
