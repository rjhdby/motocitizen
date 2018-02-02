package motocitizen.user

object User {
    var role = Role.RO
    var name = ""
    var id = 0
    var isAuthorized = false

    val isModerator: Boolean
        inline get() = role.isModerator

    val isStandard: Boolean
        inline get() = role.isStandard

    val roleName: String
        inline get() = role.text
}
