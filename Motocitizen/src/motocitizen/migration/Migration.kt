package motocitizen.migration

import android.content.Context

object Migration {
    private val helpers = arrayOf<MigrationInterface>(
            PreferencesMigration
                                                     )

    fun makeMigration(context: Context) {
        helpers.forEach {
            it.process(context)
        }
    }
}