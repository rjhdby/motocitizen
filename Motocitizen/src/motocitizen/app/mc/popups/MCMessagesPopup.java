package motocitizen.app.mc.popups;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCMessage;
import motocitizen.utils.MCUtils;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

public class MCMessagesPopup extends MCPopupWindow {
	public static PopupWindow getPopupWindow(int id, int acc_id) {
		MCMessage m = MCAccidents.points.getPoint(acc_id).messages.get(id);
		content = new TableLayout(act);
		content.setOrientation(LinearLayout.HORIZONTAL);
		content.setBackgroundColor(0xFF202020);
		content.setLayoutParams(lp);
		textToCopy = m.owner + ": " + m.text;
		content.addView(copyButtonRow(), lp);
		for (String phone : MCUtils.getPhonesFromText(m.text)) {
			content.addView(phoneButtonRow(phone), lp);
		}

		pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		pw.setOutsideTouchable(true);
		pw.setContentView(content);
		return pw;
	}
}
