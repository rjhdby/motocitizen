package motocitizen.app.general.popups;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
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

abstract class PopupWindowGeneral {

    static final String CALL_PREFIX = "Вызов: ";
    static final String SMS_PREFIX  = "СМС: ";

    final TableRow.LayoutParams layoutParams;
    TableLayout content;
    PopupWindow popupWindow;
    Context context;

    PopupWindowGeneral(Context context) {
        this.context = context;
        layoutParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        content = new TableLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020);
        content.setLayoutParams(layoutParams);
        popupWindow = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
    }

    TableRow shareMessage(final Context context, final String textToShare) {
        TableRow tr = new TableRow(content.getContext());
        Button   b  = new Button(content.getContext());
        b.setText(R.string.share);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
                popupWindow.dismiss();
            }
        });
        tr.addView(b, layoutParams);
        return tr;
    }

    TableRow copyButtonRow(final Context context, final String textToCopy) {
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
                popupWindow.dismiss();
            }
        });
        tr.addView(b, layoutParams);
        return tr;
    }

    TableRow phoneButtonRow(final Context context, String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(CALL_PREFIX + phone);
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String number = (String) ((Button) v).getText();
                intent.setData(Uri.parse("tel:" + number.replace(CALL_PREFIX, "")));
                context.startActivity(intent);
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow smsButtonRow(final Context context, String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(SMS_PREFIX + phone);
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String number = (String) ((Button) v).getText();
                intent.setData(Uri.parse("sms:" + number.replace(SMS_PREFIX, "")));
                context.startActivity(intent);
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow finishButtonRow(final Context context, final Accident point) {
        Button finish = new Button(content.getContext());
        if (point.isEnded()) {
            finish.setText(R.string.unfinish);
        } else {
            finish.setText(R.string.finish);
        }

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                if (point.isEnded()) {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
                } else {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ENDED);
                }
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow hideButtonRow(final Context context, final Accident point) {
        Button finish = new Button(content.getContext());
        if (point.isHidden()) {
            finish.setText(R.string.show);
        } else {
            finish.setText(R.string.hide);
        }

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                if (point.isHidden()) {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.ACTIVE);
                } else {
                    new AccidentChangeState(null, context, point.getId(), AccidentChangeState.HIDE);
                }
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow coordinatesButtonRow(final Context context, final Accident point) {
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
                popupWindow.dismiss();
                Toast.makeText(context, "координаты скопированы в буфер обмена", Toast.LENGTH_LONG).show();
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(coordinates, layoutParams);
        return tr;
    }

    TableRow banButtonRow(final Context context, final int id) {
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
                popupWindow.dismiss();
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(ban, layoutParams);
        return tr;
    }
}
