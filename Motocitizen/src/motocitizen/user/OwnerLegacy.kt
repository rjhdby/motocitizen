package motocitizen.user

class OwnerLegacy(val id: Int, val name: String) {
    val isUser: Boolean
        get() = id == User.dirtyRead().id
}
