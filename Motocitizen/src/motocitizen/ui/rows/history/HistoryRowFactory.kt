package motocitizen.ui.rows.history

import android.content.Context
import motocitizen.content.history.History

object HistoryRowFactory {
    fun make(context: Context, history: History): HistoryRow = HistoryRow(context, history)
}