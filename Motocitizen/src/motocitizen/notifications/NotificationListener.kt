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
    lateinit private var notificationManager: NotificationManagerCompat
    lateinit private var accident: Accident
    private var idHash: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        try {
            val id = Integer.parseInt(data["id"].toString())
            Content.requestSingleAccident(id) {
                raiseNotification(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun raiseNotification(id: Int) {
        accident = Content[id] ?: return
        if (doNotShow()) return
        notificationManager = NotificationManagerCompat.from(this)

        idHash = accident.coordinates.hashCode()

        notificationManager.notify(idHash, AccidentNotificationBuilder(this, accident).build())
        manageTray()
    }

    private fun doNotShow(): Boolean = !accident.isVisible() || preferences.doNotDisturb


    private fun manageTray() {
        tray.push(idHash)
        while (tray.size > Preferences.maxNotifications) {
            val remove = tray.pollLast()
            notificationManager.cancel(remove)
        }
    }


}
