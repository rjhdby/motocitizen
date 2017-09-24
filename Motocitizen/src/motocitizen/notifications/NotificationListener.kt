package motocitizen.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.preferences.Preferences
import motocitizen.datasources.preferences.Preferences.Stored.*
import motocitizen.dictionary.Medicine
import motocitizen.main.R
import motocitizen.ui.activity.AccidentDetailsActivity
import java.util.*

class NotificationListener : FirebaseMessagingService() {
    private val preferences = Preferences
    private var notificationManager: NotificationManagerCompat? = null
    private var accident: Accident? = null
    private var idHash: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage!!.data
        val id = Integer.parseInt(data["id"].toString())
        Content.requestSingleAccident(id) { raiseNotification(id) }
    }

    private fun raiseNotification(id: Int?) {
        accident = Content.accident(id!!)
        notificationManager = NotificationManagerCompat.from(this)
        if (doNotShow()) return

        idHash = accident!!.coordinates.hashCode()

        notificationManager!!.notify(idHash, makeNotification())
        manageTray()
    }

    private fun doNotShow(): Boolean = accident == null || !accident!!.isVisible() || preferences.doNotDisturb

    private fun makeIntent(): Intent {
        val intent = Intent(this, AccidentDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident!!.id)
        return intent
    }

    private fun makeNotification(): Notification {
        return NotificationCompat.Builder(this)
                .setContentIntent(makePendingIntent())
                .setSmallIcon(ICON)
                .setLargeIcon(makeLargeIcon())
                .setTicker(accident!!.address)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(makeTitle())
                .setSound(makeSound())
                .setVibrate(makeVibration())
                .setContentText(accident!!.address)
                .build()
    }

    private fun makePendingIntent(): PendingIntent = PendingIntent.getActivity(this, idHash, makeIntent(), PendingIntent.FLAG_ONE_SHOT)

    private fun makeSound(): Uri? {
        return if (preferences.soundTitle == "default system")
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        else
            preferences.sound
    }

    private fun makeVibration(): LongArray {
        return if (VIBRATION.boolean())
            longArrayOf(1000, 1000, 1000)
        else
            LongArray(0)
    }

    private fun makeTitle(): String = String.format("%s%s(%s)", accident!!.type.text, makeDamageString(), accident!!.distanceString())

    private fun makeDamageString(): String = if (accident!!.medicine === Medicine.UNKNOWN) "" else ", " + accident!!.medicine.text

    private fun manageTray() {
        tray.push(idHash)
        //        while (tray.size() > Preferences.Stored.MAX_NOTIFICATIONS.int()) {
        while (tray.size > MAX_NOTIFICATIONS.int()) {
            val remove = tray.pollLast()
            notificationManager!!.cancel(remove)
        }
    }

    private fun makeLargeIcon(): Bitmap = BitmapFactory.decodeResource(resources, ICON)

    companion object {
        private val ICON = R.mipmap.ic_launcher
        private val tray = LinkedList<Int>()
    }
}
