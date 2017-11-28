package motocitizen.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.NotificationCompat
import motocitizen.content.accident.Accident
import motocitizen.datasources.preferences.Preferences
import motocitizen.dictionary.Medicine
import motocitizen.main.R
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.utils.distanceString

class AccidentNotificationBuilder(val context: Context, val accident: Accident) {
    private val ICON = R.mipmap.ic_launcher

    fun build(): Notification = NotificationCompat.Builder(context)
            .setContentIntent(makePendingIntent())
            .setSmallIcon(ICON)
            .setLargeIcon(makeLargeIcon())
            .setTicker(accident.address)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentTitle(makeTitle())
            .setSound(makeSound())
            .setVibrate(makeVibration())
            .setContentText(accident.address)
            .build()

    private fun makePendingIntent(): PendingIntent = PendingIntent.getActivity(context, accident.coordinates.hashCode(), makeIntent(), PendingIntent.FLAG_ONE_SHOT)

    private fun makeSound(): Uri? = if (Preferences.soundTitle == "default system")
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    else
        Preferences.sound

    private fun makeVibration(): LongArray = if (Preferences.vibration)
        longArrayOf(1000, 1000, 1000)
    else
        LongArray(0)

    private fun makeTitle(): String = String.format("%s%s(%s)", accident.type.text, makeDamageString(), accident.distanceString())

    private fun makeLargeIcon(): Bitmap = BitmapFactory.decodeResource(context.resources, ICON)

    private fun makeIntent(): Intent {
        val intent = Intent(context, AccidentDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.id)
        return intent
    }

    private fun makeDamageString(): String = if (accident.medicine === Medicine.UNKNOWN) "" else ", " + accident.medicine.text
}