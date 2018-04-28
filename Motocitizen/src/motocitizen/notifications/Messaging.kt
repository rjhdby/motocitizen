package motocitizen.notifications

import com.google.firebase.messaging.FirebaseMessaging

object Messaging {
    private const val COMMON = "accidents"
    private const val TEST = "test"

    fun subscribe() = FirebaseMessaging.getInstance().subscribeToTopic(COMMON)

    fun subscribeToTest() = FirebaseMessaging.getInstance().subscribeToTopic(TEST)

    fun unSubscribeFromTest() = FirebaseMessaging.getInstance().unsubscribeFromTopic(TEST)

}