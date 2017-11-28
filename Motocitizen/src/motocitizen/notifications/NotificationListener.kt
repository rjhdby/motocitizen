package motocitizen.notifications

import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.preferences.Preferences
import java.util.*

class NotificationListener : FirebaseMessagingService() {

    private val tray = LinkedList<Int>()

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

        notificationManager!!.notify(idHash, AccidentNotificationBuilder(this, accident!!).build())
        manageTray()
    }

    private fun doNotShow(): Boolean = accident == null || !accident!!.isVisible() || preferences.doNotDisturb


    private fun manageTray() {
        tray.push(idHash)
        //        while (tray.size() > Preferences.Stored.MAX_NOTIFICATIONS.int()) {
        while (tray.size > Preferences.maxNotifications) {
            val remove = tray.pollLast()
            notificationManager!!.cancel(remove)
        }
    }


}
