package motocitizen.app.general.popups;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.AccidentMessage;
import motocitizen.startup.Startup;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    public static PopupWindow getPopupWindow(Context context,int id, int acc_id) {
        AccidentMessage m = AccidentsGeneral.points.getPoint(acc_id).messages.get(id);
        content = new TableLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020);
        content.setLayoutParams(lp);
        textToCopy = m.owner + ": " + m.text;
        content.addView(copyButtonRow(), lp);
        for (String phone : MyUtils.getPhonesFromText(m.text)) {
            content.addView(phoneButtonRow(phone), lp);
        }

        pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        pw.setOutsideTouchable(true);
        pw.setContentView(content);
        return pw;
    }
}
