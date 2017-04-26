package motocitizen.rows.accidentList

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

import motocitizen.accident.Accident
import motocitizen.main.R

class CommonRow : Row {
    constructor(context: Context, accident: Accident) : super(context, R.layout.accident_row, accident)

    override fun makeActive() {
        makeActive(R.drawable.message_row)
    }

    public override fun makeHidden() {
        makeHidden(R.drawable.accident_row_hidden)
    }

    public override fun makeEnded() {
        makeEnded(R.drawable.accident_row_ended)
    }

    override fun layoutParams(): FrameLayout.LayoutParams {
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(4, 2, 16, 2)
        return lp
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
