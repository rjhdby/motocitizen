package motocitizen.ui.views

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet


class IntEditTextPreference(context: Context, attrs: AttributeSet, defStyle: Int) : EditTextPreference(context, attrs, defStyle) {
    override fun getPersistedString(defaultReturnValue: String?): String = getPersistedInt(-1).toString()
    override fun persistString(value: String): Boolean = persistInt(Integer.valueOf(value))
}