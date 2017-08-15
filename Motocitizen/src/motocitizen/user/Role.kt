package motocitizen.user

internal enum class Role constructor(val code: Int, val text: String) {
    RO(0, "только чтение"),
    //    BANNED("banned", "забанен"),
    STANDARD(1, "пользователь"),
    MODERATOR(2, "модератор"),
    //    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER(3, "разработчик");

    val isStandard: Boolean
        get() = this >= STANDARD

    val isModerator: Boolean
        get() = this >= MODERATOR

    companion object {

        fun parse(role: Int): Role {
            return Role.values().firstOrNull { it.code == role } ?: RO
        }
    }
}
