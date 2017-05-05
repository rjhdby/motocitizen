@file:JvmName("ToastUtils")

package motocitizen.utils

import android.content.Context
import android.widget.Toast

fun show(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
