package motocitizen.notifications

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

object Messaging {
    private const val COMMON = "accidents"
    private const val TEST = "test"

    fun subscribe(): Task<Void> = FirebaseMessaging.getInstance().subscribeToTopic(COMMON)

    fun subscribeToTest(): Task<Void> = FirebaseMessaging.getInstance().subscribeToTopic(TEST)

    fun unSubscribeFromTest(): Task<Void> = FirebaseMessaging.getInstance().unsubscribeFromTopic(TEST)

}