package motocitizen.user

class Owner(val id: Int, val name: String) {
    val isUser: Boolean
        get() = id == User.dirtyRead().id
}
