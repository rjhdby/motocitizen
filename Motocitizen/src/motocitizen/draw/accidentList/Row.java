package motocitizen.draw.accidentList;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import motocitizen.accident.Accident;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.utils.MyUtils;
import motocitizen.utils.popups.AccidentListPopup;

abstract public class Row extends FrameLayout {

    protected Row(@NonNull Context context, int resourceId, Accident accident, ViewGroup parent) {
        super(context);
        setId(MyUtils.newId());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        lp.setMargins(30, 20, 30, 0);
        addView(inflate(context, resourceId, null), lp);

        if (accident.isEnded()) makeEnded();
        if (accident.isHidden()) makeHidden();
        ((TextView) findViewById(R.id.accident_row_content)).setText(context.getResources().getString(R.string.accident_row_content, accident.title()));
        ((TextView) findViewById(R.id.accident_row_time)).setText(MyUtils.getIntervalFromNowInText(context, accident.getTime()));
        ((TextView) findViewById(R.id.accident_row_unread)).setText(messagesText(accident));

        setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("accidentID", accident.getId());
            Router.goTo((Activity) context, Router.Target.DETAILS, bundle);
        });

        setOnLongClickListener(v -> {
            int viewLocation[] = new int[ 2 ];
            v.getLocationOnScreen(viewLocation);
            (new AccidentListPopup(context, accident.getId()))
                    .getPopupWindow(context)
                    .showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[ 0 ], viewLocation[ 1 ]);
            return true;
        });

    }

    abstract protected void makeHidden();

    abstract protected void makeEnded();

    protected void makeHidden(int resourceId) {
        setBackgroundResource(resourceId);
        ((TextView) findViewById(R.id.accident_row_content)).setTextColor(0x30FFFFFF);
    }

    protected void makeEnded(int resourceId) {
        setBackgroundResource(resourceId);
        ((TextView) findViewById(R.id.accident_row_content)).setTextColor(0x70FFFFFF);
    }

    protected Spanned messagesText(Accident accident) {
        String read = accident.getUnreadMessagesCount() > 0 ? String.format("<font color=#C62828><b>(%s)</b></font>", accident.getUnreadMessagesCount()) : "";
        String text = String.format("<b>%s</b>%s", accident.getMessages().size(), read);
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            return Html.fromHtml(text);
        }
    }
}
