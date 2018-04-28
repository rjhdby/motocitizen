package motocitizen.user

object User {
    private var role = Role.RO
    var name = ""
    var id = 0
    var isAuthorized = false

    val roleName: String
        get() = role.text

    fun isReadOnly() = role == Role.RO

    fun isModerator() = role in arrayOf(Role.MODERATOR, Role.DEVELOPER)

    fun notIsModerator() = !isModerator()

    fun logout() {
        name = ""
        role = Role.RO
        id = 0
        isAuthorized = false
    }

    fun authenticate(id: Int, name: String, role: Role) {
        this.id = id
        this.name = name
        this.role = role
        isAuthorized = true
    }
}
