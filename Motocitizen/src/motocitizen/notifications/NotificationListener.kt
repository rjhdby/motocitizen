package motocitizen.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.preferences.Preferences
import motocitizen.utils.BackgroundScope
import java.util.LinkedList

class NotificationListener : FirebaseMessagingService() {
    companion object {
        private val tray = LinkedList<Int>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var idHash: Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val id = remoteMessage.data["id"]?.toInt() ?: return
        BackgroundScope.IO().launch {
            Content.requestSingleAccident(id) {
                raiseNotification(id)
            }
        }
    }

    private fun raiseNotification(id: Int) {
        val accident = Content[id] ?: return
        if (doNotShow(accident)) return
        notificationManager = NotificationManagerCompat.from(this)

        idHash = accident.coordinates.hashCode()
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        if (permission != PackageManager.PERMISSION_GRANTED) return
        notificationManager.notify(idHash, AccidentNotificationBuilder(this, accident).build())
        manageTray()
    }

    private fun doNotShow(accident: Accident): Boolean =
        !accident.isVisible() || Preferences.doNotDisturb

    private fun manageTray() {
        tray.push(idHash)
        while (tray.size > Preferences.maxNotifications) {
            val remove = tray.pollLast() ?: continue
            notificationManager.cancel(remove)
        }
    }
}
