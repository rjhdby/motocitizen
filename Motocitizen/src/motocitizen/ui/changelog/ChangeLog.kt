package motocitizen.ui.changelog

/*
 * Special thanks to Karsten Priegnitz for idea of this class
 * https://code.google.com/p/android-change-log/
 */

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.webkit.WebView
import motocitizen.main.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ChangeLog {
    private val LIST_NONE: Byte = 0
    private val LIST_ORDERED: Byte = 1
    private val LIST_UNORDERED: Byte = 2

    private val sb = StringBuffer()
    private var currentListMode = LIST_NONE

    fun getDialog(context: Context): AlertDialog {
        val wv = WebView(context)

        wv.setBackgroundColor(Color.BLACK)
        wv.loadDataWithBaseURL(null, getLog(context), "text/html", "UTF-8", null)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Dialog))
        builder.setTitle("Что нового").setView(wv).setCancelable(false).setPositiveButton("ОК") { _, _ -> }
        return builder.create()
    }

    fun getLog(context: Context): String {
        try {
            val ins = context.resources.openRawResource(R.raw.changelog)
            val br = BufferedReader(InputStreamReader(ins))

            while (true) {
                var line: String? = br.readLine() ?: break
                line = line!!.trim { it <= ' ' }
                val marker = if (line.isNotEmpty()) line[0] else '\u0000'
                if (marker == '$') {
                    closeList()
                    continue
                }
                when (marker) {
                    '#'  -> openList(LIST_ORDERED)
                    '*'  -> openList(LIST_UNORDERED)
                    else -> closeList()
                }
                when (marker) {
                    '%'      -> sb.append("<div class='title'>").append(line.substring(1).trim { it <= ' ' }).append("</div>\n") // line contains version title
                    '_'      -> sb.append("<div class='subtitle'>").append(line.substring(1).trim { it <= ' ' }).append("</div>\n") // line contains version title
                    '!'      -> sb.append("<div class='freetext'>").append(line.substring(1).trim { it <= ' ' }).append("</div>\n") // line contains free text
                    '#', '*' -> sb.append("<li>").append(line.substring(1).trim { it <= ' ' }).append("</li>\n")// line contains numbered list item
                    else     -> sb.append(line).appendln() // no special character: just use line as is
                }
            }
            closeList()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    private fun openList(listMode: Byte) {
        if (currentListMode != listMode) {
            closeList()
            when (listMode) {
                LIST_ORDERED   -> sb.append("<div class='list'><ol>\n")
                LIST_UNORDERED -> sb.append("<div class='list'><ul>\n")
            }
        }
        currentListMode = listMode
    }

    private fun closeList() {
        when (currentListMode) {
            LIST_ORDERED   -> sb.append("</ol></div>\n")
            LIST_UNORDERED -> sb.append("</ul></div>\n")
        }
        currentListMode = LIST_NONE
    }
}