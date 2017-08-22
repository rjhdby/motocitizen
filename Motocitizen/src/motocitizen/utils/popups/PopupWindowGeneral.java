package motocitizen.utils.popups;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.content.accident.Accident;
import motocitizen.main.R;
import motocitizen.network.requests.ActivateAccident;
import motocitizen.network.requests.BanRequest;
import motocitizen.network.requests.EndAccident;
import motocitizen.network.requests.HideAccident;
import motocitizen.router.Router;
import motocitizen.utils.LocationUtils;
import motocitizen.utils.ToastUtils;

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
        b.setOnClickListener(v -> {
            Router.INSTANCE.share((Activity) context, textToShare);
            popupWindow.dismiss();
        });
        tr.addView(b, layoutParams);
        return tr;
    }

    TableRow copyButtonRow(final Context context, final String textToCopy) {
        TableRow tr = new TableRow(content.getContext());
        Button   b  = new Button(content.getContext());
        b.setText(R.string.copy);
        b.setOnClickListener(v -> {
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", textToCopy);
            myClipboard.setPrimaryClip(myClip);
            popupWindow.dismiss();
        });
        tr.addView(b, layoutParams);
        return tr;
    }

    TableRow phoneButtonRow(final Context context, final String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(context.getString(R.string.popup_dial, phone));
        dial.setOnClickListener(v -> {
            popupWindow.dismiss();
            Router.INSTANCE.dial((Activity) context, phone);
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow smsButtonRow(final Context context, final String phone) {
        Button dial = new Button(content.getContext());
        dial.setText(context.getString(R.string.popup_sms, phone));
        dial.setOnClickListener(v -> {
            popupWindow.dismiss();
            Router.INSTANCE.sms((Activity) context, phone);
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(dial, layoutParams);
        return tr;
    }

    TableRow finishButtonRow(final Accident point) {
        Button finish = new Button(content.getContext());
        finish.setText(point.isEnded() ? R.string.unfinish : R.string.finish);

        finish.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (point.isEnded()) {
                new ActivateAccident(point.getId(), null);
            } else {
                new EndAccident(point.getId(), null);
            }
//            new AccidentChangeStateRequest(point.isEnded() ? AccidentStatus.ACTIVE.getCode() : AccidentStatus.ENDED.getCode(), point.getId(), null);
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow hideButtonRow(final Accident point) {
        Button finish = new Button(content.getContext());
        finish.setText(point.isHidden() ? R.string.show : R.string.hide);

        finish.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (point.isHidden()) {
                new ActivateAccident(point.getId(), null);
            } else {
                new HideAccident(point.getId(), null);
            }
//            new AccidentChangeStateRequest(point.isHidden() ? AccidentStatus.ACTIVE.getCode() : AccidentStatus.HIDDEN.getCode(), point.getId(), null);
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(finish, layoutParams);
        return tr;
    }

    TableRow coordinatesButtonRow(final Context context, final Accident point) {
        Button coordinates = new Button(content.getContext());
        coordinates.setText(R.string.copy_coordinates);
        coordinates.setOnClickListener(v -> {
            LatLng           latlng = LocationUtils.Location2LatLng(point.getLocation());
            String           text   = String.valueOf(latlng.latitude) + "," + String.valueOf(latlng.longitude);
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);
            popupWindow.dismiss();
            ToastUtils.show(context, context.getString(R.string.coordinates_copied));
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(coordinates, layoutParams);
        return tr;
    }

    TableRow banButtonRow(final Context context, final int id) {
        Button ban = new Button(content.getContext());
        ban.setText("Забанить");
        //TODO разобраться с пирамидой зла
        ban.setOnClickListener(v -> {
            new BanRequest(id, result -> ((Activity) context).runOnUiThread(() -> ToastUtils.show(context, result.has("error") ? "Ошибка связи с сервером" : "Пользователь забанен")));
            popupWindow.dismiss();
        });
        TableRow tr = new TableRow(content.getContext());
        tr.addView(ban, layoutParams);
        return tr;

        /*
                    switch (response.getString("ban")) {
                case "OK":
                    return "Статус изменен";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка " + response.toString();
                case "NO USER":
                    return "Пользователь не зарегистрирован";
                case "AUTH ERROR":
                case "NO RIGHTS":
                    return "Недостаточно прав";
            }
         */
    }
}
