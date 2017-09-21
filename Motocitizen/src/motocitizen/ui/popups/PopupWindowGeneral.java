package motocitizen.ui.popups;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.maps.model.LatLng;

import kotlin.Unit;
import motocitizen.content.accident.Accident;
import motocitizen.datasources.network.ApiResponse;
import motocitizen.datasources.network.requests.ActivateAccident;
import motocitizen.datasources.network.requests.BanRequest;
import motocitizen.datasources.network.requests.EndAccident;
import motocitizen.datasources.network.requests.HideAccident;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.utils.ToastUtils;

abstract public class PopupWindowGeneral extends PopupWindow {
    private static final ColorDrawable         windowBackGround = new ColorDrawable(0x00ffffff);
    private static final int                   contentColor     = 0xFF202020;
    final protected      TableRow.LayoutParams layoutParams     = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private         Context     context;
    final protected TableLayout rootView;

    PopupWindowGeneral(Context context) {
        this.context = context;
        rootView = new TableLayout(context);
        rootView.setOrientation(LinearLayout.HORIZONTAL);
        rootView.setBackgroundColor(contentColor);
        rootView.setLayoutParams(layoutParams);
        setContentView(rootView);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(windowBackGround);
        setOutsideTouchable(true);
    }

    protected TableRow shareMessageView(final String textToShare) {
        return makeButton(R.string.share, v -> shareButtonPressed(textToShare));
    }

    private void shareButtonPressed(String text) {
        Router.INSTANCE.share((Activity) context, text);
        dismiss();
    }

    protected TableRow copyButtonView(String text) {
        return makeButton(R.string.copy, v -> copyButtonPressed(text));
    }

    private void copyButtonPressed(String text) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(ClipData.newPlainText("text", text));
        dismiss();
    }

    protected TableRow phoneButtonView(String phone) {
        return makeButton(context.getString(R.string.popup_dial, phone), v -> phoneButtonPressed(phone));
    }

    private void phoneButtonPressed(String phone) {
        Router.INSTANCE.dial((Activity) context, phone);
        dismiss();
    }

    protected TableRow smsButtonView(String phone) {
        return makeButton(context.getString(R.string.popup_sms, phone), v -> smsButtonPressed(phone));
    }

    private void smsButtonPressed(String phone) {
        Router.INSTANCE.sms((Activity) context, phone);
        dismiss();
    }

    protected TableRow finishButtonView(Accident point) {
        return makeButton(point.isEnded() ? R.string.unfinish : R.string.finish, v -> finishButtonPressed(point));
    }

    private void finishButtonPressed(Accident point) {
        if (point.isEnded()) {
            new ActivateAccident(point.getId(), response -> Unit.INSTANCE);
        } else {
            new EndAccident(point.getId(), response -> Unit.INSTANCE);
        }
        dismiss();
    }

    protected TableRow hideButtonView(Accident point) {
        return makeButton(point.isHidden() ? R.string.show : R.string.hide, v -> hideButtonPressed(point));
    }

    private void hideButtonPressed(Accident point) {
        if (point.isHidden()) {
            new ActivateAccident(point.getId(), response -> Unit.INSTANCE);
        } else {
            new HideAccident(point.getId(), response -> Unit.INSTANCE);
        }
        dismiss();
    }

    protected TableRow coordinatesButtonView(Accident point) {
        return makeButton(R.string.copy_coordinates, v -> coordinatesButtonPressed(point));
    }

    private void coordinatesButtonPressed(Accident point) {
        LatLng           latlng      = point.getCoordinates();
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(ClipData.newPlainText("text", String.format("%s,%s", latlng.latitude, latlng.longitude)));
        ToastUtils.show(context, context.getString(R.string.coordinates_copied));
        dismiss();
    }

    protected TableRow banButtonView(int id) {
        return makeButton("Забанить", v -> banButtonPressed(id));
    }

    private void banButtonPressed(int id) {
        new BanRequest(id, this::banRequestCallback);
        dismiss();
    }

    private Unit banRequestCallback(ApiResponse response) {
        ((Activity) context).runOnUiThread(
                () -> ToastUtils.show(context,
                                      response.hasError()
                                      ? "Ошибка связи с сервером"
                                      : "Пользователь забанен")
                                          );
        return Unit.INSTANCE;
    }

    private TableRow makeButton(String text, View.OnClickListener listener) {
        Button button = new Button(context);
        button.setText(text);
        button.setOnClickListener(listener);
        TableRow tableRow = new TableRow(context);
        tableRow.addView(button, layoutParams);
        return tableRow;
    }

    private TableRow makeButton(int text, View.OnClickListener listener) {
        return makeButton(context.getString(text), listener);
    }
}
