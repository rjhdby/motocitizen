package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import motocitizen.content.accident.AccidentBuilder
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface

class DescriptionFrame(val context: FragmentActivity, val accidentBuilder: AccidentBuilder, val callback: () -> Unit) : FrameInterface {
    private val ROOT_VIEW = R.id.create_final_frame
    private val CREATE_BUTTON = R.id.CREATE
    private val confirmButton = context.findViewById(CREATE_BUTTON) as Button
    private val view = context.findViewById<View>(ROOT_VIEW)

    init {
        confirmButton.isEnabled = false
        confirmButton.setOnClickListener { _ -> callback() }
        (context.findViewById(R.id.create_final_text) as EditText).addTextChangedListener(finalTextWatcher())
    }

    override fun show() {
        view.visibility = View.VISIBLE
        setConfirmButtonStatus()
    }

    override fun hide() {
        view.visibility = View.INVISIBLE
        confirmButton.isEnabled = false
    }

    private fun finalTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                accidentBuilder.description(s.toString())
                setConfirmButtonStatus()
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    private fun setConfirmButtonStatus() {
        confirmButton.isEnabled = accidentBuilder.type.isAccident() || accidentBuilder.description.length > 6
    }
}