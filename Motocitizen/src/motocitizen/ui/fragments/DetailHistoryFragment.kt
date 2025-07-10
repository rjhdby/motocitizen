package motocitizen.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.main.R
import motocitizen.ui.activity.AccidentDetailsActivity.Companion.ACCIDENT_ID_KEY
import motocitizen.ui.rows.HistoryRow

class DetailHistoryFragment : FragmentForAccident() {
    companion object {
        private val ROOT_LAYOUT = R.layout.fragment_detail_history
        private val CONTENT_VIEW = R.id.details_log_content
    }

    private lateinit var rootView: View
    private val logContent: LinearLayout by lazy { rootView.findViewById(CONTENT_VIEW)!! }
    private lateinit var accident: Accident

    override fun setAccident(accident: Accident) {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(ROOT_LAYOUT, container, false)
        redrawHistory()
        return rootView
    }

    private fun redrawHistory() {
        logContent.removeAllViews()
        accident.history.forEach {
            try {
                logContent.addView(HistoryRow(requireActivity(), it))
            } catch (e: IllegalArgumentException) {
                //todo fuckup
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content[savedInstanceState.getInt(ACCIDENT_ID_KEY)]!!
    }
}