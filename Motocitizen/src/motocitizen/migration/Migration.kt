package motocitizen.migration

import android.content.Context
import android.preference.PreferenceManager

object Migration {
    private val helpers = arrayOf<MigrationInterface>(
            PreferencesMigration
                                                     )

    fun makeMigration(context: Context) {
        val old = PreferenceManager.getDefaultSharedPreferences(context).getInt("mc.app.version", 0)
        val new = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        helpers.forEach {
            it.process(context, old, new)
        }
    }
}