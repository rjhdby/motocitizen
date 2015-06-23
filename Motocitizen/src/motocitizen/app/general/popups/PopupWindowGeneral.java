package motocitizen.app.general.popups;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.general.Accident;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentChangeState;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.BanRequest;
import motocitizen.utils.MyUtils;

class PopupWindowGeneral {

    static final String CALL_PREFIX = "Вызов: ";
    static final String SMS_PREFIX  = "СМС: ";

    static final TableRow.LayoutParams lp = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    static         TableLayout content;
    static         PopupWindow pw;
    static         String      textToCopy;
    private static Context     context;

    private static Accident point;

    static TableRow shareMessage(Context outerContext) {
        context = outerContext;
        TableRow tr = new TableRow(content.getContext());
        Button   b  = new Button(content.getContext());
        b.setText(R.string.share);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToCopy);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
                pw.dismiss();
            }
        });
        tr.addView(b, lp);
        return tr;
    }

    static TableRow copyButtonRow() {
        TableRow tr = new TableRow(content.getContext());
        Button   b  = new Button(content.getContext());
        b.setText(R.string.copy);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", textToCopy);
                myClipboard.setPrimaryClip(myClip);
                pw.dismiss();
            }
        });
        tr.addView(b, lp);
        return tr;
    }

    static TableRow phoneButtonRow(String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(CALL_PREFIX + phone);
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String number = (String) ((Button) v).getText();
                intent.setData(Uri.parse("tel:" + number.replace(CALL_PREFIX, "")));
                context.startActivity(intent);
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, lp);
        return tr;
    }

    static TableRow smsButtonRow(String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(SMS_PREFIX + phone);
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String number = (String) ((Button) v).getText();
                intent.setData(Uri.parse("sms:" + number.replace(SMS_PREFIX, "")));
                context.startActivity(intent);
            }
        });
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

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
                if (point.isEnded()) {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
                } else {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ENDED);
                }
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, lp);
        return tr;
    }

    static TableRow hideButtonRow(final Accident point) {
        Button finish = new Button(content.getContext());
        if (point.isHidden()) {
            finish.setText(R.string.show);
        } else {
            finish.setText(R.string.hide);
        }

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
                if (point.isHidden()) {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
                } else {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.HIDE);
                }
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, lp);
        return tr;
    }

    static TableRow coordinatesButtonRow(final Accident point) {
        Button coordinates = new Button(content.getContext());
        coordinates.setText("Скопировать координаты");
        coordinates.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng           latlng = MyUtils.LocationToLatLng(point.getLocation());
                String           text   = String.valueOf(latlng.latitude) + "," + String.valueOf(latlng.longitude);
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                pw.dismiss();
                Toast.makeText(context, "координаты скопированы в буфер обмена", Toast.LENGTH_LONG).show();
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(coordinates, lp);
        return tr;
    }

    static TableRow banButtonRow(final int id) {
        Button ban = new Button(content.getContext());
        ban.setText("Забанить");
        ban.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new BanRequest(context, new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(JSONObject result) throws JSONException {
                        if (result.has("error")) {
                            try {
                                Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(context, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "Пользователь забанен", Toast.LENGTH_LONG).show();
                        }
                    }
                }, id);
                pw.dismiss();
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(ban, lp);
        return tr;
    }
}
