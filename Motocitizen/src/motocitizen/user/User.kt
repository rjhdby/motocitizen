package motocitizen.user

import motocitizen.datasources.network.ApiResponse

object User {
    var role = Role.RO
    var name = ""
    var id = 0
    var isAuthorized = false

    fun auth(login: String, password: String, callback: (ApiResponse) -> Unit) {
        Auth.auth(login, password, callback)
    }

    fun logoff() {
        name = ""
        role = Role.RO
        id = 0
        isAuthorized = false
    }

    val isModerator: Boolean
        get() = role.isModerator

    val isStandard: Boolean
        get() = role.isStandard

    val roleName: String
        get() = role.text
}
