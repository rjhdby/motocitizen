package motocitizen.ui.views

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet


class IntEditTextPreference : EditTextPreference {
    override fun getPersistedString(defaultReturnValue: String?): String = getPersistedInt(-1).toString()
    override fun persistString(value: String): Boolean = persistInt(Integer.valueOf(value))

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
}