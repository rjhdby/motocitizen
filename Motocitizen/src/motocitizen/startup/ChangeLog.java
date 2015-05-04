package motocitizen.startup;
/*
 * Special thanx to Karsten Priegnitz for idea of this class
 * https://code.google.com/p/android-change-log/
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import motocitizen.main.R;

public class ChangeLog {

    private static final String EOCL = "END_OF_CHANGE_LOG";
    private static String lastVersion;
    private static StringBuffer sb = null;
    private static Listmode currentListMode = Listmode.NONE;

    public static AlertDialog getDialog(Context context, boolean full) {
        WebView wv = new WebView(context);

        wv.setBackgroundColor(Color.BLACK);
        wv.loadDataWithBaseURL(null, getLog(context, full), "text/html", "UTF-8",
                null);
        Log.d("LOG", wv.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(context, android.R.style.Theme_Dialog));
        builder.setTitle("Что нового").setView(wv).setCancelable(false)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        return builder.create();
    }

    ;

    public static String getLog(Context context, boolean full) {
        lastVersion = (new MCPreferences(context).getCurrentVersion());
        sb = new StringBuffer();
        try {
            InputStream ins = context.getResources().openRawResource(R.raw.changelog);
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));

            String line = null;
            boolean advanceToEOVS = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                char marker = line.length() > 0 ? line.charAt(0) : 0;
                if (marker == '$') {
                    closeList();
                    String version = line.substring(1).trim();
                    // stop output?
                    if (!full) {
                        if (lastVersion.equals(version)) {
                            advanceToEOVS = true;
                        } else if (version.equals(EOCL)) {
                            advanceToEOVS = false;
                        }
                    }
                } else if (!advanceToEOVS) {
                    switch (marker) {
                        case '%':
                            // line contains version title
                            closeList();
                            sb.append("<div class='title'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '_':
                            // line contains version title
                            closeList();
                            sb.append("<div class='subtitle'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '!':
                            // line contains free text
                            closeList();
                            sb.append("<div class='freetext'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '#':
                            // line contains numbered list item
                            openList(Listmode.ORDERED);
                            sb.append("<li>" + line.substring(1).trim() + "</li>\n");
                            break;
                        case '*':
                            // line contains bullet list item
                            openList(Listmode.UNORDERED);
                            sb.append("<li>" + line.substring(1).trim() + "</li>\n");
                            break;
                        default:
                            // no special character: just use line as is
                            closeList();
                            sb.append(line + "\n");
                    }
                }
            }
            closeList();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static void openList(Listmode listMode) {
        if (currentListMode != listMode) {
            closeList();
            if (listMode == Listmode.ORDERED) {
                sb.append("<div class='list'><ol>\n");
            } else if (listMode == Listmode.UNORDERED) {
                sb.append("<div class='list'><ul>\n");
            }
        }
        currentListMode = listMode;
    }

    private static void closeList() {
        if (currentListMode == Listmode.ORDERED) {
            sb.append("</ol></div>\n");
        } else if (currentListMode == Listmode.UNORDERED) {
            sb.append("</ul></div>\n");
        }
        currentListMode = Listmode.NONE;
    }

    private enum Listmode {
        NONE, ORDERED, UNORDERED,
    }

}