package motocitizen.utils;
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
    /* constants */
    private static final String EOCL = "END_OF_CHANGE_LOG";
    /* end constants */

    private static StringBuffer sb;
    private static ListMode     currentListMode;

    static {
        sb = null;
        currentListMode = ListMode.NONE;
    }

    public static AlertDialog getDialog(Context context) {
        WebView wv = new WebView(context);

        wv.setBackgroundColor(Color.BLACK);
        wv.loadDataWithBaseURL(null, getLog(context, true), "text/html", "UTF-8", null);
        Log.d("LOG", wv.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Dialog));
        builder.setTitle("Что нового").setView(wv).setCancelable(false).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    public static String getLog(Context context, boolean full) {
        String lastVersion = String.valueOf(Preferences.getInstance().getAppVersion());
        sb = new StringBuffer();
        try {
            InputStream    ins = context.getResources().openRawResource(R.raw.changelog);
            BufferedReader br  = new BufferedReader(new InputStreamReader(ins));

            String  line;
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
                            sb.append("<div class='title'>").append(line.substring(1).trim()).append("</div>\n");
                            break;
                        case '_':
                            // line contains version title
                            closeList();
                            sb.append("<div class='subtitle'>").append(line.substring(1).trim()).append("</div>\n");
                            break;
                        case '!':
                            // line contains free text
                            closeList();
                            sb.append("<div class='freetext'>").append(line.substring(1).trim()).append("</div>\n");
                            break;
                        case '#':
                            // line contains numbered list item
                            openList(ListMode.ORDERED);
                            sb.append("<li>").append(line.substring(1).trim()).append("</li>\n");
                            break;
                        case '*':
                            // line contains bullet list item
                            openList(ListMode.UNORDERED);
                            sb.append("<li>").append(line.substring(1).trim()).append("</li>\n");
                            break;
                        default:
                            // no special character: just use line as is
                            closeList();
                            sb.append(line).append("\n");
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

    private static void openList(ListMode listMode) {
        if (currentListMode != listMode) {
            closeList();
            if (listMode == ListMode.ORDERED) {
                sb.append("<div class='list'><ol>\n");
            } else if (listMode == ListMode.UNORDERED) {
                sb.append("<div class='list'><ul>\n");
            }
        }
        currentListMode = listMode;
    }

    private static void closeList() {
        switch (currentListMode) {
            case ORDERED:
                sb.append("</ol></div>\n");
                break;
            case UNORDERED:
                sb.append("</ul></div>\n");
                break;
        }
        currentListMode = ListMode.NONE;
    }

    private enum ListMode {
        NONE,
        ORDERED,
        UNORDERED,
    }
}