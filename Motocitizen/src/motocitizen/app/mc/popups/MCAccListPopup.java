package motocitizen.app.mc.popups;

import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.user.MCRole;
import motocitizen.utils.Const;
import motocitizen.utils.MCUtils;

public class MCAccListPopup extends MCPopupWindow {
    public static PopupWindow getPopupWindow(int id) {
        MCPoint p = MCAccidents.points.getPoint(id);
        content = new TableLayout(act);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020);
        content.setLayoutParams(lp);
        textToCopy = Const.dateFormat.format(p.created) + ". " + p.getTypeText() + ". " + p.getMedText() + ". " + p.address + ". " + p.descr;
        content.addView(copyButtonRow(), lp);

        for (String phone : MCUtils.getPhonesFromText(p.descr)) {
            content.addView(phoneButtonRow(phone), lp);
        }

        if (MCRole.isStandart()) {
            content.addView(finishButtonRow(p));
        }
        if (MCRole.isModerator()) {
            content.addView(hideButtonRow(p));
        }
        pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        pw.setOutsideTouchable(true);
        pw.setContentView(content);
        return pw;
    }
}
