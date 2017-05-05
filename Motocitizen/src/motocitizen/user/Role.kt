package motocitizen.user

internal enum class Role private constructor(val code: String, val text: String) {
    RO("readonly", "только чтение"),
    BANNED("banned", "забанен"),
    STANDARD("standart", "пользователь"),
    MODERATOR("moderator", "модератор"),
    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER("developer", "разработчик");

    val isStandard: Boolean
        get() = this >= STANDARD

    val isModerator: Boolean
        get() = this >= MODERATOR

    companion object {

        fun parse(role: String): Role {
            return Role.values().firstOrNull { it.code == role } ?: RO
        }
    }
}
