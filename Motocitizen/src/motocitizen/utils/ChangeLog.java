package motocitizen.utils;
/*
 * Special thanx to Karsten Priegnitz for idea of this class
 * https://code.google.com/p/android-change-log/
 */

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import motocitizen.main.R;

public class ChangeLog {
    private static final byte LIST_NONE      = 0;
    private static final byte LIST_ORDERED   = 1;
    private static final byte LIST_UNORDERED = 2;

    private static StringBuffer sb              = new StringBuffer();
    private static byte         currentListMode = LIST_NONE;

    public static AlertDialog getDialog(Context context) {
        WebView wv = new WebView(context);

        wv.setBackgroundColor(Color.BLACK);
        wv.loadDataWithBaseURL(null, getLog(context), "text/html", "UTF-8", null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Dialog));
        builder.setTitle("Что нового").setView(wv).setCancelable(false).setPositiveButton("ОК", (dialog, which) -> {});
        return builder.create();
    }

    public static String getLog(Context context) {
        try {
            InputStream    ins = context.getResources().openRawResource(R.raw.changelog);
            BufferedReader br  = new BufferedReader(new InputStreamReader(ins));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                char marker = line.length() > 0 ? line.charAt(0) : 0;
                if (marker == '$') {
                    closeList();
                } else {
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
                            openList(LIST_ORDERED);
                            sb.append("<li>").append(line.substring(1).trim()).append("</li>\n");
                            break;
                        case '*':
                            // line contains bullet list item
                            openList(LIST_UNORDERED);
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

    private static void openList(byte listMode) {
        if (currentListMode != listMode) {
            closeList();
            if (listMode == LIST_ORDERED) {
                sb.append("<div class='list'><ol>\n");
            } else if (listMode == LIST_UNORDERED) {
                sb.append("<div class='list'><ul>\n");
            }
        }
        currentListMode = listMode;
    }

    private static void closeList() {
        switch (currentListMode) {
            case LIST_ORDERED:
                sb.append("</ol></div>\n");
                break;
            case LIST_UNORDERED:
                sb.append("</ul></div>\n");
                break;
        }
        currentListMode = LIST_NONE;
    }
}