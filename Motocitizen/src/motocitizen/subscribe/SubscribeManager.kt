package motocitizen.subscribe

object SubscribeManager {
    enum class Event {
        ACCIDENTS_UPDATED,
        LOCATION_UPDATED
    }

    private val subscribers: HashMap<Event, HashMap<String, () -> Unit>> = HashMap()
    private val events: HashSet<Event> = HashSet()

    fun subscribe(type: Event, tag: String, callback: () -> Unit) {
        if (!subscribers.containsKey(type)) subscribers[type] = HashMap()
        subscribers[type]?.put(tag, callback)
        if (events.contains(type)) runSubscribers(type)
    }

    fun unSubscribeAll(tag: String) = subscribers.values.forEach { it.remove(tag) }

    fun fireEvent(type: Event) {
        events.add(type)
        runSubscribers(type)
    }

    private fun runSubscribers(type: Event) {
        if (subscribers[type]?.isEmpty() != false) return
        if (events.contains(type)) {
            subscribers[type]?.values?.forEach { it() }
            events.remove(type)
        }
    }
}