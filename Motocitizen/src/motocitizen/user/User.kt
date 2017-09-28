package motocitizen.user

object User {
    var role = Role.RO
    var name = ""
    var id = 0
    var isAuthorized = false

    val isModerator: Boolean
        get() = role.isModerator

    val isStandard: Boolean
        get() = role.isStandard

    val roleName: String
        get() = role.text
}
