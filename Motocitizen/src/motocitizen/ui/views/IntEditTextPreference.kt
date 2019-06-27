package motocitizen.ui.views

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet


class IntEditTextPreference : EditTextPreference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun getPersistedString(defaultReturnValue: String?): String = getPersistedInt(-1).toString()
    override fun persistString(value: String): Boolean = persistInt(Integer.valueOf(value))
}