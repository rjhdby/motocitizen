package motocitizen.utils.popups;

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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.accident.Accident;
import motocitizen.content.AccidentStatus;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.AccidentChangeStateRequest;
import motocitizen.network.requests.BanRequest;
import motocitizen.utils.MyUtils;
import motocitizen.utils.ShowToast;

abstract class PopupWindowGeneral {

    final TableRow.LayoutParams layoutParams;
    final TableLayout           content;
    final PopupWindow           popupWindow;

    PopupWindowGeneral(Context context) {
        layoutParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        content = new TableLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackgroundColor(0xFF202020); //TODO ёбаный стыд
        content.setLayoutParams(layoutParams);
        popupWindow = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ffffff)); //TODO ёбаный стыд 2
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

    TableRow phoneButtonRow(final Context context, final String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(context.getString(R.string.popup_dial, phone));
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(intent);
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow smsButtonRow(final Context context, final String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(context.getString(R.string.popup_sms, phone));
        dial.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phone));
                context.startActivity(intent);
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow finishButtonRow(final Accident point) {
        Button finish = new Button(content.getContext());
        finish.setText(point.isEnded() ? R.string.unfinish : R.string.finish);

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                new AccidentChangeStateRequest(null, point.getId(), point.isEnded() ? AccidentStatus.ACTIVE.toCode() : AccidentStatus.ENDED.toCode());
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow hideButtonRow(final Accident point) {
        Button finish = new Button(content.getContext());
        finish.setText(point.isHidden() ? R.string.show : R.string.hide);

        finish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                new AccidentChangeStateRequest(null, point.getId(), point.isHidden() ? AccidentStatus.ACTIVE.toCode() : AccidentStatus.HIDDEN.toCode());
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow coordinatesButtonRow(final Context context, final Accident point) {
        Button coordinates = new Button(content.getContext());
        coordinates.setText(R.string.copy_coordinates);
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
                ShowToast.message(context, context.getString(R.string.coordinates_copied));
            }
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(coordinates, layoutParams);
        return tr;
    }

    TableRow banButtonRow(final Context context, final int id) {
        Button ban = new Button(content.getContext());
        ban.setText("Забанить");
        //TODO разобраться с пирамидой зла
        ban.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new BanRequest(new AsyncTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(JSONObject result) throws JSONException {
                        if (result.has("error")) {
                            try {
                                ShowToast.message(context, result.getString("error"));
                            } catch (JSONException e) {
                                ShowToast.message(context, "Неизвестная ошибка");
                                e.printStackTrace();
                            }
                        } else {
                            ShowToast.message(context, "Пользователь забанен");
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
