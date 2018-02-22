package motocitizen.user

object User {
    var role = Role.RO
    var name = ""
    var id = 0
    var isAuthorized = false

    fun isReadOnly() = role == Role.RO

    fun notIsReadOnly() = !isReadOnly()

    fun isModerator() = role.isModerator

    fun notIsModerator() = !isModerator()
}
