package motocitizen.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import motocitizen.activity.AccidentDetailsActivity
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.main.R
import motocitizen.rows.history.HistoryRowFactory


class DetailHistoryFragment() : Fragment() {
    private val ROOT_LAYOUT = R.layout.fragment_detail_history
    private val CONTENT_VIEW = R.id.details_log_content
    private lateinit var logContent: LinearLayout
    private lateinit var accident: Accident

    constructor(accident: Accident) : this() {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(ROOT_LAYOUT, container, false)
        logContent = rootView.findViewById(CONTENT_VIEW) as LinearLayout

        redrawHistory()
        return rootView
    }

    private fun redrawHistory() {
        logContent.removeAllViews()
        accident.history.forEach { logContent.addView(HistoryRowFactory.make(activity, it)) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content.accidents[savedInstanceState.getInt(AccidentDetailsActivity.ACCIDENT_ID_KEY)]!!
    }
}
