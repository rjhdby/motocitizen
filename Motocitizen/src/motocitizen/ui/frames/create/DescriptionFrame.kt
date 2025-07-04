package motocitizen.ui.frames.create

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import motocitizen.content.accident.AccidentBuilder
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface
import motocitizen.utils.hide
import motocitizen.utils.show

class DescriptionFrame(val context: FragmentActivity, val accidentBuilder: AccidentBuilder, val callback: () -> Unit) : FrameInterface {
    private val confirmButton: Button = context.findViewById(R.id.CREATE)
    private val view = context.findViewById<View>(R.id.create_final_frame)

    init {
        confirmButton.isEnabled = false
        confirmButton.setOnClickListener { callback() }
        (context.findViewById<EditText>(R.id.create_final_text)!!).addTextChangedListener(finalTextWatcher())
    }

    override fun show() {
        view.show()
        setConfirmButtonStatus()
    }

    override fun hide() {
        view.hide()
        confirmButton.isEnabled = false
    }

    private fun finalTextWatcher(): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            accidentBuilder.description = s.toString()
            setConfirmButtonStatus()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun setConfirmButtonStatus() {
        confirmButton.isEnabled = accidentBuilder.type.isAccident() || accidentBuilder.description.length > 6
    }
}