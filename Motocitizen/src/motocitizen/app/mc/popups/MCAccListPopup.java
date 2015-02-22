package motocitizen.app.mc.popups;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoints;
import motocitizen.app.mc.user.MCRole;
import motocitizen.core.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

public class MCAccListPopup extends MCPopupWindow{
	public static PopupWindow getPopupWindow(Point p){
		content = new TableLayout(act);
		content.setOrientation(LinearLayout.HORIZONTAL);
		content.setBackgroundColor(0xFF202020);
		content.setLayoutParams(lp);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			textToCopy = MCAccidents.points.getTime(p.id) + ". " + p.get("mc_accident_type") + ". " + p.get("mc_accident_med") + ". " + p.get("address") + ". "
					+ p.get("descr");
			content.addView(copyButtonRow(), lp);
		}
		String phonesString = p.get("descr").replaceAll("[^0-9]", "");
		Matcher matcher = Pattern.compile("[7|8][0-9]{10}").matcher(phonesString);
		while (matcher.find()) {
			content.addView(phoneButtonRow(matcher.group()), lp);
		}
		if(MCRole.isStandart()){
			content.addView(finishButtonRow(p));
		}
		if(MCRole.isModerator()){
			content.addView(hideButtonRow(p));
		}
		pw = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		pw.setOutsideTouchable(true);
		pw.setContentView(content);
		return pw;
	}
}
