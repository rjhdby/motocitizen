package motocitizen.app.general.popups;

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

import motocitizen.app.general.Accident;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentChangeState;
import motocitizen.startup.Startup;

class PopupWindowGeneral {

    static final String CALL_PREFIX = "Вызов: ";
    static final String SMS_PREFIX = "СМС: ";

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
            myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", textToCopy);
            myClipboard.setPrimaryClip(myClip);
            pw.dismiss();
        }
    };
    private static Accident point;
    private static final OnClickListener dialButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            String number = (String) ((Button) v).getText();
            intent.setData(Uri.parse("tel:" + number.replace(CALL_PREFIX, "")));
            context.startActivity(intent);
        }
    };
    private static final OnClickListener smsButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String number = (String) ((Button) v).getText();
            intent.setData(Uri.parse("sms:" + number.replace(SMS_PREFIX, "")));
            context.startActivity(intent);
        }
    };
    private static final OnClickListener finishButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            if (point.isEnded()) {
                new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
            } else {
                new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ENDED);
            }
        }
    };
    private static final OnClickListener hideButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            if (point.isHidden()) {
                new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
            } else {
                new AccidentChangeState(null, context, point.getId(), AccidentChangeState.HIDE);
            }
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

    static TableRow shareMessage(Context outerContext){
        context = outerContext;
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

    static TableRow phoneButtonRow(String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(CALL_PREFIX + phone);
        dial.setOnClickListener(dialButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, lp);
        return tr;
    }

    static TableRow smsButtonRow(String raw) {
        String phone = raw.substring(1);
        Button dial = new Button(content.getContext());
        dial.setText(SMS_PREFIX + phone);
        dial.setOnClickListener(smsButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, lp);
        return tr;
    }

    static TableRow finishButtonRow(Accident p) {
        point = p;
        Button finish = new Button(content.getContext());
        if (point.isEnded()) {
            finish.setText(R.string.unfinish);
        } else {
            finish.setText(R.string.finish);
        }

        finish.setOnClickListener(finishButtonListener);
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, lp);
        return tr;
    }

    static TableRow hideButtonRow(Accident p) {
        point = p;
        Button finish = new Button(content.getContext());
        if (point.isHidden()) {
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
