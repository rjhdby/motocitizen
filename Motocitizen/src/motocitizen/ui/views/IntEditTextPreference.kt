package motocitizen.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference


class IntEditTextPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : EditTextPreference(context, attrs) {

    override fun getPersistedString(defaultReturnValue: String?): String = getPersistedInt(-1).toString()

    override fun persistString(value: String): Boolean = persistInt(Integer.valueOf(value))
}