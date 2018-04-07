package motocitizen.notifications

import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.preferences.Preferences
import java.util.*

class NotificationListener : FirebaseMessagingService() {
    companion object {
        private val tray = LinkedList<Int>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var idHash: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val id = remoteMessage.data["id"]?.toInt() ?: return
        Content.requestSingleAccident(id) { raiseNotification(id) }
    }

    private fun raiseNotification(id: Int) {
        val accident = Content[id] ?: return
        if (doNotShow(accident)) return
        notificationManager = NotificationManagerCompat.from(this)

        idHash = accident.coordinates.hashCode()

        notificationManager.notify(idHash, AccidentNotificationBuilder(this, accident).build())
        manageTray()
    }

    private fun doNotShow(accident: Accident): Boolean = !accident.isVisible() || Preferences.doNotDisturb

    private fun manageTray() {
        tray.push(idHash)
        while (tray.size > Preferences.maxNotifications) {
            val remove = tray.pollLast()
            notificationManager.cancel(remove)
        }
    }
}
