package motocitizen.migration

import android.content.Context

interface MigrationInterface {
    fun process(context: Context)
}