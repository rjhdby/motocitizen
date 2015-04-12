package motocitizen.app.mc.popups;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;

class MCPopupWindow {
    static final TableRow.LayoutParams lp = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    static TableLayout content;
    static PopupWindow pw;
    static String textToCopy;
    private static Context context;
    private static final OnClickListener copyButtonListener = new OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) Startup.context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", textToCopy);
            myClipboard.setPrimaryClip(myClip);
            pw.dismiss();
        }
    };
    private static MCPoint point;
    private static final OnClickListener dialButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + ((Button) v).getText()));
            Startup.context.startActivity(intent);
        }
    };
    private static final OnClickListener finishButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Map<String, String> params = new HashMap<>();
            params.put("login", MCAccidents.auth.getLogin());
            params.put("passhash", MCAccidents.auth.makePassHash());
            if (point.status.equals("acc_status_end")) {
                params.put("state", "acc_status_act");
            } else {
                params.put("state", "acc_status_end");
            }
            params.put("id", String.valueOf(point.id));
            new JSONCall("mcaccidents", "changeState").request(params);
            MCAccidents.refresh(v.getContext());
        }
    };
    private static final OnClickListener hideButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Map<String, String> params = new HashMap<>();
            params.put("login", MCAccidents.auth.getLogin());
            params.put("passhash", MCAccidents.auth.makePassHash());
            if (point.status.equals("acc_status_hide")) {
                params.put("state", "acc_status_act");
            } else {
                params.put("state", "acc_status_hide");
            }
            params.put("id", String.valueOf(point.id));
            new JSONCall("mcaccidents", "changeState").request(params);
            MCAccidents.refresh(v.getContext());
        }
    };

    private static final OnClickListener shareButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToCopy);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
            pw.dismiss();
        }
    };

    static TableRow shareMessage(Context outherContext){
        context = outherContext;
        TableRow tr = new TableRow(content.getContext());
        Button b = new Button(content.getContext());
        b.setText(R.string.share);
        b.setOnClickListener(shareButtonListener);
        tr.addView(b, lp);
        return tr;
    }

    static TableRow copyButtonRow() {
        TableRow tr = new TableRow(content.getContext());
        Button b = new Button(content.getContext());
        b.setText(R.string.copy);
        b.setOnClickListener(copyButtonListener);
        tr.addView(b, lp);
        return tr;
    }

    static TableRow phoneButtonRow(String raw) {
        String phone = "+7" + raw.substring(1);
        Button dial = new Button(content.getContext());
        dial.setText(phone);
        dial.setOnClickListener(dialButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, lp);
        return tr;
    }

    static TableRow finishButtonRow(MCPoint p) {
        point = p;
        Button finish = new Button(content.getContext());
        if (point.status.equals("acc_status_end")) {
            finish.setText(R.string.unfinish);
        } else {
            finish.setText(R.string.finish);
        }

        finish.setOnClickListener(finishButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, lp);
        return tr;
    }

    static TableRow hideButtonRow(MCPoint p) {
        point = p;
        Button finish = new Button(content.getContext());
        if (point.status.equals("acc_status_hide")) {
            finish.setText(R.string.show);
        } else {
            finish.setText(R.string.hide);
        }

        finish.setOnClickListener(hideButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, lp);
        return tr;
    }
}
